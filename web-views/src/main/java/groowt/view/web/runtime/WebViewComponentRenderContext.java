package groowt.view.web.runtime;

import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.RenderContext;
import groowt.view.web.WebViewComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface WebViewComponentRenderContext extends RenderContext {

    @ApiStatus.Internal
    ViewComponent createFragment(WebViewComponent fragment, WebViewComponentChildCollectorClosure cl);

}
