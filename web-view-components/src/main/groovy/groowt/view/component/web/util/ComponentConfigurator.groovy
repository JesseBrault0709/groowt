package groowt.view.component.web.util

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groowt.view.component.web.WebViewComponent
import groowt.view.component.web.WebViewComponentContext

class ComponentConfigurator {

    private final WebViewComponent self

    ComponentConfigurator(WebViewComponent self) {
        this.self = self
    }

    void context(
            @DelegatesTo(ContextConfigurator)
            @ClosureParams(value = SimpleType, options = 'groowt.view.component.web.WebViewComponentContext')
            Closure configureContext
    ) {
        //noinspection GroovyAssignabilityCheck
        WebViewComponentContext context = self.context
        configureContext.delegate = new ContextConfigurator(context)
        configureContext(context)
    }

}
