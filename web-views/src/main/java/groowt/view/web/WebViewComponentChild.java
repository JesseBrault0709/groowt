package groowt.view.web;

import groovy.lang.Closure;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.runtime.DefaultComponentWriter;

import java.io.Writer;

public class WebViewComponentChild {

    private static final class ChildRenderClosure extends Closure<Void> {

        private final ViewComponent parent;
        private final Object child;
        private final ComponentWriter writer;

        public ChildRenderClosure(
                ComponentTemplate template,
                ViewComponent parent,
                ComponentWriter writer,
                Object child
        ) {
            super(template, template);
            this.parent = parent;
            this.child = child;
            this.writer = writer;
            this.setDelegate(this.parent);
            this.setResolveStrategy(Closure.DELEGATE_FIRST);
        }

        public ViewComponent getParent() {
            return this.parent;
        }

        public void doCall() {
            this.writer.append(this.child);
        }

    }

    private final ComponentTemplate template;
    private final ComponentWriter componentWriter;
    private final Object child;

    public WebViewComponentChild(ComponentTemplate template, ComponentWriter componentWriter, Object child) {
        this.template = template;
        this.componentWriter = componentWriter;
        this.child = child;
    }

    public Object getChild() {
        return this.child;
    }

    public void render(ViewComponent parent) {
        new ChildRenderClosure(this.template, parent, this.componentWriter, this.child).call();
    }

    public Writer renderTo(Writer out, ViewComponent parent) {
        final var componentWriter = new DefaultComponentWriter(out);
        final var childRenderClosure = new ChildRenderClosure(this.template, parent, componentWriter, this.child);
        childRenderClosure.call();
        return out;
    }

}
