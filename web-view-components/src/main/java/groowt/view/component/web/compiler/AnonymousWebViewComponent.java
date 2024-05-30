package groowt.view.component.web.compiler;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.web.WebViewComponent;
import org.jetbrains.annotations.ApiStatus;

import java.io.Writer;
import java.util.List;

@ApiStatus.Internal
public final class AnonymousWebViewComponent implements WebViewComponent {

    @Override
    public ComponentTemplate getComponentTemplate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setComponentTemplate(ComponentTemplate componentTemplate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContext(ComponentContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentContext getContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renderTo(Writer writer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> getChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChildren(List<?> children) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renderChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renderChildren(Writer to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renderChildren(ComponentWriter to) {
        throw new UnsupportedOperationException();
    }

}
