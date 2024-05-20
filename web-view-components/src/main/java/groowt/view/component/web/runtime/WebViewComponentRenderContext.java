package groowt.view.component.web.runtime;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.RenderContext;
import groowt.view.component.web.WebViewComponent;

import java.util.Map;

public interface WebViewComponentRenderContext extends RenderContext {

    @SuppressWarnings("unused")
    Map<String, Object> EMPTY_ATTR = Map.of();

    @SuppressWarnings("unused")
    Object[] EMPTY_CONSTRUCTOR_ARGS = {};

    WebViewComponent create(
            Resolved<? extends WebViewComponent> resolved,
            Map<String, Object> attr,
            Object[] constructorArgs
    );

    WebViewComponent create(
            Resolved<? extends WebViewComponent> resolved,
            Map<String, Object> attr,
            Object[] constructorArgs,
            Closure<Void> childrenClosure
    );

    ViewComponent createFragment(WebViewComponent fragment, Closure<Void> childrenClosure);

}
