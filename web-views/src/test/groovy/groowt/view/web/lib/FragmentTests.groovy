package groowt.view.web.lib

import groowt.view.component.ComponentContext
import groowt.view.component.ComponentFactory
import groowt.view.web.DefaultWebViewComponent
import groowt.view.web.DefaultWebViewComponentContext
import groowt.view.web.WebViewTemplateComponentSource
import org.junit.jupiter.api.Test

class FragmentTests extends AbstractWebViewComponentTests {

    static class Greeter extends DefaultWebViewComponent {

        String greeting

        Greeter(Map<String, Object> attr) {
            super(WebViewTemplateComponentSource.of('$greeting'))
            greeting = attr.greeting
        }

    }

    private final ComponentContext greeterContext = new DefaultWebViewComponentContext().tap {
        pushDefaultScope()
        def greeterFactory = ComponentFactory.ofClosure { type, componentContext, attr ->
            new Greeter(attr)
        }
        currentScope.add('Greeter', greeterFactory)
    }

    @Test
    void simple() {
        this.doTest('<><Greeter greeting="Hello, World!" /></>', 'Hello, World!', this.greeterContext)
    }

    @Test
    void multipleChildren() {
        this.doTest(
                '''
                <>
                    <Greeter greeting='Hello, one!' />&nbsp;<Greeter greeting='Hello, two!' />
                </>
                '''.stripIndent(), 'Hello, one!&nbsp;Hello, two!', this.greeterContext
        )
    }

}
