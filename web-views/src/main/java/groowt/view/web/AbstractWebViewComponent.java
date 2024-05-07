package groowt.view.web;

import groovy.lang.Closure;
import groowt.view.component.AbstractViewComponent;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.compiler.ComponentTemplateCompiler;
import groowt.view.component.factory.ComponentTemplateSource;
import groowt.view.web.compiler.WebViewComponentTemplateCompiler;
import groowt.view.web.runtime.DefaultWebViewComponentWriter;
import groowt.view.web.runtime.WebViewComponentWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractWebViewComponent extends AbstractViewComponent implements WebViewComponent {

    private List<WebViewChildRenderer> childRenderers;

    public AbstractWebViewComponent() {}

    public AbstractWebViewComponent(ComponentTemplate template) {
        super(template);
    }

    public AbstractWebViewComponent(Class<? extends ComponentTemplate> templateClass) {
        super(templateClass);
    }

    protected AbstractWebViewComponent(ComponentTemplateSource source, WebViewComponentTemplateCompiler compiler) {
        super(source, compiler);
    }

    @SuppressWarnings("unchecked")
    protected AbstractWebViewComponent(
            ComponentTemplateSource source,
            Function<? super Class<? extends AbstractWebViewComponent>, ? extends ComponentTemplateCompiler> compilerFunction
    ) {
        super(source, selfClass -> compilerFunction.apply((Class<AbstractWebViewComponent>) selfClass));
    }

    @Override
    public List<WebViewChildRenderer> getChildRenderers() {
        if (this.childRenderers == null) {
            this.childRenderers = new ArrayList<>();
        }
        return this.childRenderers;
    }

    @Override
    public boolean hasChildren() {
        return !this.getChildRenderers().isEmpty();
    }

    @Override
    public void setChildRenderers(List<WebViewChildRenderer> children) {
        this.childRenderers = children;
    }

    @Override
    public void renderChildren() {
        for (final var childRenderer : this.getChildRenderers()) {
            try {
                if (childRenderer instanceof WebViewChildComponentRenderer childComponentRenderer) {
                    this.getContext().beforeComponentRender(childComponentRenderer.getComponent());
                }
                childRenderer.render(this);
            } catch (Exception e) {
                throw new ChildRenderException(e);
            } finally {
                if (childRenderer instanceof WebViewChildComponentRenderer childComponentRenderer) {
                    this.getContext().afterComponentRender(childComponentRenderer.getComponent());
                }
            }
        }
    }

    @Override
    public void renderTo(Writer out) throws IOException {
        final WebViewComponentWriter webWriter = new DefaultWebViewComponentWriter(out);
        final Closure<?> renderer = this.getTemplate().getRenderer();
        renderer.setDelegate(this);
        renderer.setResolveStrategy(Closure.DELEGATE_FIRST);
        this.beforeRender();
        renderer.call(this.getContext(), webWriter);
        this.afterRender();
    }

}
