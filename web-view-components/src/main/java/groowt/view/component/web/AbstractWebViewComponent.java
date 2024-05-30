package groowt.view.component.web;

import groovy.lang.Closure;
import groowt.view.component.AbstractViewComponent;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.compiler.ComponentTemplateCompileUnit;
import groowt.view.component.compiler.source.ComponentTemplateSource;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.runtime.DefaultComponentWriter;
import groowt.view.component.web.compiler.DefaultWebViewComponentTemplateCompileUnit;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractWebViewComponent extends AbstractViewComponent implements WebViewComponent {

    private List<Object> children;

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
        this(selfClass -> new DefaultWebViewComponentTemplateCompileUnit(
                source.getDescriptiveName(), selfClass, source, selfClass.getPackageName())
        );
    }

    @Override
    public List<Object> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    @Override
    public boolean hasChildren() {
        return !this.getChildren().isEmpty();
    }

    @Override
    public void setChildren(List<?> children) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.addAll(children);
    }

    @Override
    public void renderChildren() {
        this.renderChildren(this.getContext().getRenderContext().getWriter());
    }

    @Override
    public void renderChildren(Writer to) {
        final ComponentWriter componentWriter = new DefaultComponentWriter(to);
        componentWriter.setComponentContext(this.getContext());
        componentWriter.setRenderContext(this.getContext().getRenderContext());
        this.renderChildren(componentWriter);
    }

    @Override
    public void renderChildren(ComponentWriter to) {
        for (final var child : this.getChildren()) {
            try {
                to.append(child);
            } catch (Exception e) {
                throw new ChildRenderException(e);
            }
        }
    }

    @Override
    public void renderTo(Writer out) throws IOException {
        final ComponentWriter webWriter = new DefaultComponentWriter(out);
        final Closure<?> renderer = this.getComponentTemplate().getRenderer();
        renderer.setDelegate(this);
        renderer.setResolveStrategy(Closure.DELEGATE_FIRST);
        this.beforeRender();
        renderer.call(this.getContext(), webWriter);
        this.afterRender();
    }

}
