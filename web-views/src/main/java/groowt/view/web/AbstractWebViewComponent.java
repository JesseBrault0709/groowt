package groowt.view.web;

import groovy.lang.Closure;
import groowt.view.component.AbstractViewComponent;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.compiler.ComponentTemplateCompileUnit;
import groowt.view.component.compiler.source.ComponentTemplateSource;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.runtime.DefaultComponentWriter;
import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractWebViewComponent extends AbstractViewComponent implements WebViewComponent {

    private List<WebViewComponentChild> childRenderers;

    public AbstractWebViewComponent() {}

    public AbstractWebViewComponent(ComponentTemplate template) {
        super(template);
    }

    public AbstractWebViewComponent(Class<? extends ComponentTemplate> templateClass) {
        super(templateClass);
    }

    public AbstractWebViewComponent(
            Function<? super Class<? extends AbstractViewComponent>, ComponentTemplateCompileUnit> compileUnitFunction
    ) {
        super(compileUnitFunction);
    }

    public AbstractWebViewComponent(ComponentTemplateSource source) {
        this(selfClass -> new WebViewComponentTemplateCompileUnit(selfClass, source, selfClass.getPackageName()));
    }

    @Override
    public List<WebViewComponentChild> getChildren() {
        if (this.childRenderers == null) {
            this.childRenderers = new ArrayList<>();
        }
        return this.childRenderers;
    }

    @Override
    public boolean hasChildren() {
        return !this.getChildren().isEmpty();
    }

    @Override
    public void setChildren(List<WebViewComponentChild> children) {
        this.childRenderers = children;
    }

    @Override
    public void renderChildren() {
        for (final var childRenderer : this.getChildren()) {
            try {
                childRenderer.render(this);
            } catch (Exception e) {
                throw new ChildRenderException(e);
            }
        }
    }

    @Override
    public void renderTo(Writer out) throws IOException {
        final ComponentWriter webWriter = new DefaultComponentWriter(out);
        final Closure<?> renderer = this.getTemplate().getRenderer();
        renderer.setDelegate(this);
        renderer.setResolveStrategy(Closure.DELEGATE_FIRST);
        this.beforeRender();
        renderer.call(this.getContext(), webWriter);
        this.afterRender();
    }

}
