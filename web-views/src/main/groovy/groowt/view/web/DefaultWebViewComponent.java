package groowt.view.web;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groowt.view.component.AbstractViewComponent;
import groowt.view.component.ComponentContext;
import groowt.view.component.ComponentTemplate;
import groowt.view.web.WebViewTemplateComponentSource.*;
import groowt.view.web.runtime.WebViewComponentWriter;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultWebViewComponent extends AbstractViewComponent implements WebViewComponent {

    private static ComponentTemplate getComponentTemplate(
            Class<? extends DefaultWebViewComponent> selfType,
            WebViewTemplateComponentSource source,
            @Nullable GroovyClassLoader groovyClassLoader
    ) {
        final var compiler = new DefaultWebComponentTemplateCompiler(
                CompilerConfiguration.DEFAULT,
                selfType.getPackageName()
        );

        if (groovyClassLoader != null) {
            compiler.setGroovyClassLoader(groovyClassLoader);
        }

        return switch (source) {
            case FileSource(File f) -> compiler.compile(selfType, f);
            case InputStreamSource(InputStream inputStream) -> compiler.compile(selfType, inputStream);
            case ReaderSource(Reader r) -> compiler.compile(selfType, r);
            case StringSource(String s) -> compiler.compile(selfType, s);
            case URISource(URI uri) -> compiler.compile(selfType, uri);
            case URLSource(URL url) -> compiler.compile(selfType, url);
        };
    }

    private ComponentContext context;
    private List<WebViewChildRenderer> children;

    public DefaultWebViewComponent() {}

    public DefaultWebViewComponent(ComponentTemplate template) {
        super(template);
    }

    public DefaultWebViewComponent(Class<? extends ComponentTemplate> templateType) {
        super(templateType);
    }

    public DefaultWebViewComponent(WebViewTemplateComponentSource source) {
        this.setTemplate(getComponentTemplate(this.getClass(), source, null));
    }

    public DefaultWebViewComponent(WebViewTemplateComponentSource source, GroovyClassLoader groovyClassLoader) {
        this.setTemplate(getComponentTemplate(this.getClass(), source, groovyClassLoader));
    }

    @Override
    public void setContext(ComponentContext context) {
        this.context = context;
    }

    @Override
    public ComponentContext getContext() {
        return Objects.requireNonNull(this.context);
    }

    @Override
    public List<WebViewChildRenderer> getChildRenderers() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    @Override
    public boolean hasChildren() {
        return !this.getChildRenderers().isEmpty();
    }

    @Override
    public void setChildRenderers(List<WebViewChildRenderer> children) {
        this.children = children;
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
        final var webWriter = new WebViewComponentWriter(out);
        final Closure<?> renderer = this.getTemplate().getRenderer();
        renderer.setDelegate(this);
        renderer.setResolveStrategy(Closure.DELEGATE_FIRST);
        renderer.call(this.getContext(), webWriter);
    }

}
