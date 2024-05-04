package groowt.view.component;

import groovy.lang.Closure;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public abstract class AbstractViewComponent implements ViewComponent {

    private ComponentTemplate template;

    public AbstractViewComponent() {}

    public AbstractViewComponent(ComponentTemplate template) {
        this.template = Objects.requireNonNull(template);
    }

    public AbstractViewComponent(Class<? extends ComponentTemplate> templateClass) {
        try {
            this.template = templateClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ComponentTemplate getTemplate() {
        return Objects.requireNonNull(template);
    }

    protected void setTemplate(ComponentTemplate template) {
        this.template = Objects.requireNonNull(template);
    }

    protected void beforeRender() {
        this.getContext().beforeComponentRender(this);
    }

    protected void afterRender() {
        this.getContext().afterComponentRender(this);
    }

    @Override
    public void renderTo(Writer out) throws IOException {
        final Closure<?> closure = this.template.getRenderer();
        closure.setDelegate(this);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        this.beforeRender();
        closure.call(this.getContext(), out);
        this.afterRender();
    }

}
