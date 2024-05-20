package groowt.view.component.web

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

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
