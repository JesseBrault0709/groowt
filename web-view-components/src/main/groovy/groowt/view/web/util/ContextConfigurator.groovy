package groowt.view.web.util

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groowt.view.web.DefaultWebViewComponentScope
import groowt.view.web.WebViewComponentContext

class ContextConfigurator {

    private final WebViewComponentContext context

    ContextConfigurator(WebViewComponentContext context) {
        this.context = context
    }

    void rootScope(
            @DelegatesTo(DefaultWebViewComponentScope)
            @ClosureParams(value = SimpleType, options = 'groowt.view.web.WebViewComponentScope')
            Closure configureRootScope
    ) {
        //noinspection GroovyAssignabilityCheck
        DefaultWebViewComponentScope rootScope = context.rootScope
        configureRootScope.delegate = rootScope
        configureRootScope(rootScope)
    }

}
