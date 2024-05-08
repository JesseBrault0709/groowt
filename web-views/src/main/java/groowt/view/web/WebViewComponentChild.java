package groowt.view.web;

import groovy.lang.Closure;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.ComponentWriter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WebViewComponentChild {

    private static final class ChildRenderClosure extends Closure<Void> {

        private final ViewComponent parent;
        private final @Nullable Object child;

        public ChildRenderClosure(ComponentTemplate template, ViewComponent parent, @Nullable Object child) {
            super(template, template);
            this.parent = parent;
            this.child = child;
            this.setDelegate(this.parent);
            this.setResolveStrategy(Closure.DELEGATE_FIRST);
        }

        public ViewComponent getParent() {
            return this.parent;
        }

        public void doCall(ComponentWriter writer) {
            writer.append(Objects.requireNonNull(this.child));
        }

        public void doCall(ComponentWriter writer, Object givenChild) {
            writer.append(givenChild);
        }

    }

    private final ComponentTemplate template;
    private final ComponentWriter out;
    private final Object child;

    public WebViewComponentChild(ComponentTemplate template, ComponentWriter out, Object child) {
        this.template = template;
        this.out = out;
        this.child = child;
    }

    public Object getChild() {
        return this.child;
    }

    public void render(ViewComponent parent) {
        final var cl = this.getRenderer(parent);
        cl.call(this.child);
    }

    public Closure<Void> getRenderer(ViewComponent parent) {
        final var cl = new ChildRenderClosure(this.template, parent, null);
        return cl.curry(this.out);
    }

}
