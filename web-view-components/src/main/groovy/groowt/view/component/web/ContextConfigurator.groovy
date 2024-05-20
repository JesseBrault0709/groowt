package groowt.view.component.web

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

class ContextConfigurator {

    private final WebViewComponentContext context

    ContextConfigurator(WebViewComponentContext context) {
        this.context = context
    }

    void rootScope(
            @DelegatesTo(DefaultWebViewComponentScope)
            @ClosureParams(value = SimpleType, options = 'groowt.view.component.web.WebViewComponentScope')
            Closure configureRootScope
    ) {
        //noinspection GroovyAssignabilityCheck
        DefaultWebViewComponentScope rootScope = context.rootScope
        configureRootScope.delegate = rootScope
        configureRootScope(rootScope)
    }

}
