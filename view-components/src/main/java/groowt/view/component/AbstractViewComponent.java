package groowt.view.component;

import groovy.lang.Closure;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractViewComponent implements ViewComponent {

    private ComponentContext context;
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

    protected AbstractViewComponent(TemplateSource source, Function<String, ComponentTemplateCompiler> getCompiler) {
        final Class<? extends AbstractViewComponent> selfClass = this.getSelfClass();
        this.template = getCompiler.apply(selfClass.getPackageName()).compile(selfClass, source);
    }

    protected AbstractViewComponent(TemplateSource source, ComponentTemplateCompiler compiler) {
        this.template = compiler.compile(this.getSelfClass(), source);
    }

    protected abstract Class<? extends AbstractViewComponent> getSelfClass();

    @Override
    public void setContext(ComponentContext context) {
        this.context = context;
    }

    @Override
    public ComponentContext getContext() {
        return Objects.requireNonNull(this.context);
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

    /**
     * @implSpec If overriding, <strong>please</strong> call
     * {@link #beforeRender()}and {@link #afterRender()} before
     * and after the actual rendering is done, respectively.
     */
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
