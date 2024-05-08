package groowt.view.web.util

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groowt.view.web.WebViewComponentContext
import groowt.view.web.WebViewComponentScope

class ContextConfigurator {

    private final WebViewComponentContext context

    ContextConfigurator(WebViewComponentContext context) {
        this.context = context
    }

    void rootScope(
            @DelegatesTo(WebViewComponentScope)
            @ClosureParams(value = SimpleType, options = 'groowt.view.web.WebViewComponentScope')
            Closure configureRootScope
    ) {
        //noinspection GroovyAssignabilityCheck
        WebViewComponentScope rootScope = context.rootScope
        configureRootScope.delegate = rootScope
        configureRootScope(rootScope)
    }

}
