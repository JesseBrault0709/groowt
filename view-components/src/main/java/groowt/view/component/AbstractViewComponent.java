package groowt.view.component;

import groovy.lang.Closure;
import groowt.view.component.compiler.*;
import groowt.view.component.context.ComponentContext;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractViewComponent implements ViewComponent {

    private static final ComponentTemplateClassFactory templateClassFactory = new SimpleComponentTemplateClassFactory();

    private static ComponentTemplate instantiateTemplate(Class<? extends ComponentTemplate> templateClass) {
        try {
            return templateClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final ComponentTemplate template;
    private ComponentContext context;

    public AbstractViewComponent() {
        this.template = null;
    }

    public AbstractViewComponent(ComponentTemplate template) {
        this.template = template;
    }

    public AbstractViewComponent(Class<? extends ComponentTemplate> templateClass) {
        this.template = instantiateTemplate(templateClass);
    }

    public AbstractViewComponent(
            Function<? super Class<? extends AbstractViewComponent>, ComponentTemplateCompileUnit> compileUnitFunction
    ) {
        final ComponentTemplateCompileResult compileResult;
        try {
            compileResult = compileUnitFunction.apply(this.getClass()).compile();
        } catch (ComponentTemplateCompileException e) {
            throw new RuntimeException(e);
        }
        final var templateClass = templateClassFactory.getTemplateClass(compileResult);
        this.template = instantiateTemplate(templateClass);
    }

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

    protected void beforeRender() {}

    protected void afterRender() {}

    /**
     * @implSpec If overriding, <strong>please</strong> call
     * {@link #beforeRender()} and {@link #afterRender()} before
     * and after the actual rendering is done, respectively;
     * this way, components can still do their before/after
     * logic even if this method is overwritten.
     */
    @Override
    public void renderTo(Writer out) throws IOException {
        final Closure<?> closure = this.getTemplate().getRenderer();
        closure.setDelegate(this);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        this.beforeRender();
        closure.call(this.getContext(), out);
        this.afterRender();
    }

}
