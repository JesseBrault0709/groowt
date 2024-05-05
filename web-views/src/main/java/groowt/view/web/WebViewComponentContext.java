package groowt.view.web;

import groovy.lang.Closure;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.ViewComponent;
import org.jetbrains.annotations.ApiStatus;

public interface WebViewComponentContext extends ComponentContext {

    /**
     * For use only by compiled web view component templates.
     */
    @ApiStatus.Internal
    ViewComponent createFragment(Closure<?> childCollector);

}
