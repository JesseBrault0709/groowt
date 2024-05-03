# View Components Spec

The View Components work roughly as follows.

First, one defines a component by at a minimum implementing the `ViewComponent` interface. Additionally, for ease of use, one can extend one of the built-in helpers, such as `AbstractViewComponent` or `GStringTemplateViewComponent`. For example, here might be a web-style component:
```groovy
class MyComponent extends GStringTemplateViewComponent {
    String greeting
    
    MyComponent(Map<String, Object> attr) {
        super(new File('someTemplate.gst')) // TODO: figure out what args this actually takes
        greeting = atr.greeting
    }
}
```

Its associated template could be as simple as:
```text
Hello. Here is a friendly greeting: ${greeting}.
```

However, it could be something more complex such as a JSON document:
```json
{
    "greeting": "<%= greeting %>"
}
```

Extending this last example, say our `MyComponent` class had this method: 

```groovy
def getGreetingInQuotes() {
   '"' + greeting + '"' 
}
```

Then we could simply do in our JSON document:
```
{
    "greeting": $greetingInQuotes // or <%= greetingInQuotes %>, whichever you prefer
}
```

And so on.

Now we have to find a way to invoke our component. We could of course do so programmatically, like:

```groovy
def myComponent = new MyComponent(greeting: 'hello from everywhere!')
def rendered = myComponent.render()
assert rendered == 'Hello. Here is a friendly greeting: hello from everywhere!'
```

However, this is basically the same as using `GStringTemplateView` (from the `Views` module). For the real power of the View Components, let's see how we can call them from other templates and components.

## Using Web Components

Continuing with the `MyComponent` from above, let's create our basic template which will include a rendering of the component. It's as simple as this, say in a file called `myComponentPage.gst`:

```text
<MyComponent greeting="Hello, World!" />
```

To render this template, we need to do the following:

```groovy
import MyComponent

def pageViewComponent = new PageViewComponent(new File('myComponentPage.gst')).configure {
    context {
        registry {
            rootScope {
                addWithMapArg(MyComponent)
            }
        }
    }
}
def rendered = pageViewComponent.render()
assert rendered == 'Hello. Here is a friendly greeting: Hello, World!'
```

Internally, the `PageComponent` will compile its template to something like the following Groovy script:

```groovy
import static com.jessebrault.groowt.view.component.runtime.Helpers.*

def getScript() {
    return { Writer __writer0 ->
        resolveOrThrow(context, 'MyComponent')([greeting: 'Hello, World!']).renderTo(__writer0)
    }
}
```

The script above has the single method `getScript()` which returns a single closure. The `PageComponent` class will call this method, receive the closure, set itself as the closure's delegate, and call it with a `Writer`.

## More Advanced Web Component Example

Here is quite a broad sketch of how a set of HTML form components might work.

ViewComponent Groovy classes:

```groovy
// This trait should be included in the HTML lib
trait HTMLComponent {
    String orElseEmpty(boolean cond, Closure lazyOnTrue, Closure<String> format) {
        cond ? format(lazyOnTrue()) : ''
    }

    String orElseEmpty(boolean cond, String onTrue) {
        cond ? onTrue : ''
    }

    String orElseEmpty(boolean cond, Closure<String> onTrue) {
        cond ? onTrue() : ''
    }

    String inQuotes(Object value) {
        /"$value"/
    }
    
    String attr(String name, Object value) {
        /$name="$value"/
    }
    
    String attr(Closure cl) {
        new RenderAttrClosure(this, cl)() // RenderAttrClosure would be in the lib
    }

    String joinAttr(Map<String, Object> attr) {
        orElseEmpty(!attr.isEmpty) {
            attr.collect(this.&attr).join(' ')
        }
    }
    
    String tag(String tagName, Map<String, Object> attr, Object inner) {
        "<$tagName ${joinAttr(attr)}>$inner</$tagName>"
    }
}

// Our enhanced form class
class FormWithModel extends GStringTemplateViewComponent implements HTMLComponent {
    final String id
    final Object model
    final String action
    final boolean wrap
    final Map<String, Object> customAttr
    
    private final Closure children

    FormWithModel(Map<String, Object> attr, Closure children) {
        super(new File('formWithModel.gst'))
        this.id = id
        this.model = attr.model
        this.action = attr.action
        this.wrap = attr.wrap != null ? attr.wrap : false
        this.children = children
    }

    @Override
    protected Closure getChildren() {
        this.children
    }

    @Override
    protected ComponentRegistry.Scope getChildrenScope() {
        new SimpleScope().tap {
            addWithContextAndMapArgs(Input)
        }
    }

    boolean hasModelProperty(String name) {
        this.model.metaClass.properties.find { it.name == name } != null
    }

    Object getModelProperty(String name) {
        this.model.getProperty(name)
    }
}

class Input extends GStringTemplateViewComponent implements HTMLComponent {

    private static String getDefaultType(Object model, String name) {
        def value = model.getProperty(name)
        return switch (value) {
            case String -> 'text'
            default -> throw new UnsupportedOperationException('String model properties not supported yet')
        }
    }
    
    final String name
    final String type
    final boolean wrap
    final boolean putLabel
    final String label
    
    private final Map<String, Object> customAttr

    Input(ComponentContext context, Map<String, Object> attr) {
        super(new File('input.gst'))
        this.context = context
        this.name = requireNonNull(attr.name)
        this.type = attr.type ? attr.type : getDefaultType(this.getForm().model, this.name)
        this.wrap = attr.wrap != null ? attr.wrap : this.getForm().wrap
        this.putLabel = attr.putLabel != null ? attr.putLabel : true
        this.label = attr.label != null ? attr.label : ''
        this.customAttr = attr.findAll { keyHolder, value -> value != null && !(keyHolder in ['name', 'wrap']) }
    }

    private FormWithModel getForm() {
        def form = this.context.findNearestAncestorByClass(FormWithModel)
        if (form == null) {
            throw new ComponentException("An Input can only be used inside a FormWithModel")
        } else {
            return form
        }
    }

    boolean hasValue() {
        this.form.hasModelProperty(this.name)
    }

    Object getValue() {
        this.form.getModelProperty(this.name)
    }
}

// Should be in the HTML lib
class SurroundIf extends GStringTemplateViewComponent implements HTMLComponent {
    
    static Closure<Tuple> tag(String name, Map<String, Object> attr) {
        return { SurroundIf self ->
            Tuple.of("<$name ${self.joinAttr(attr)}>", "</$name>")
        }
    }

    private static final String template = '''
<On cond={condition}>
    <%= tag[0] %>
        <%= renderChildren() %>
    <%= tag[1] %>
</On>
'''

    final boolean condition
    final Tuple tag

    private final Closure children

    SurroundIf(Map<String, Object> attr, Closure children) {
        super(template.trim())
        this.condition = attr.condition
        switch (attr.tag) {
            case null -> { this.tag = Tuple.of('', '') }
            case Closure -> { this.tag = attr.tag.call(this) }
            case String -> { this.tag = Tuple.of("<$attr.tag>", "</$attr.tag>") }
            default -> { throw new IllegalArgumentException() }
        }
        this.children = children
    }

    @Override
    protected Closure getChildren() {
        this.children
    }
}
```

And our template files:

```text
// formWithModel.gst
<form (id, name)>
    ${ renderChildren() }
</form>
```

```text
// input.gst

<SurroundOn cond={wrap} tag={<div class="form-control">${ renderChildren() }</div>}>
    <On cond={putLabel} out={<label for={ name.capitalize() }>$label</label>} />
    <Switch expr={type}>
        <When is="text">
            <input (name, type, *customAttr) value={ hasValue() ? value : null } />
        </When>
        <When is="textarea">
            <textarea (name, *customAttr)>
                <On cond={ hasValue() } out={value} />
            </textarea>
        </When>
        <Default do={ throw new UnsupportedOperationException() } />
    </Switch>
</SurroundOn>
```

Our basic model:

```groovy
class MessageModel {
    String from
    String to
    String message
}
```

Now here is our target page:

```text
// target.gst
<FormWithModel id="message_form" model={message} action="/sendMessage" wrap={true}>
    <Input name="from" />
    <Input name="to" putLabel={false} />
    <Input name="message" type="textarea" wrap={false} label="Write your message here: " />
</FormWithModel>
```

Now let's render it:

```groovy
def message = new MessageModel(from: 'Jesse', to: 'Jeanna', message: 'Hello, World!')
def root = new HTMLRootViewComponent(new File('target.gst')).configure {
    context {
        registry {
            rootScope {
                addWithMapAndChildrenArgs(SurroundIf, FormWithModel)
            }
        }
    }
}
root.message = message
def rendered = root.render()
assert rendered == '''
<form id="message_form" action="/sendMessage">
    <div class="form-control">
        <label for="from">From</label>
        <input name="from" type="text" value="Jesse" />
    </div>
    <div class="form-control">
        <input name="to" type="text" value="Jeanna" />
    </div>
    <label for"name">Write your message here:</label>
    <textarea name="message">Hello, world!</textarea>
</form>
'''.trim() // may not be slightly correct with indentation, but close enough
```




