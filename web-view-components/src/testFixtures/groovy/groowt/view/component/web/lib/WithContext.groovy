package groowt.view.component.web.lib

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groowt.view.component.web.DefaultWebViewComponentContext
import groowt.view.component.web.WebViewComponentContext

trait WithContext {

    WebViewComponentContext context(
            @ClosureParams(value = SimpleType, options = 'groowt.view.component.web.WebViewComponentContext')
            @DelegatesTo(value = WebViewComponentContext)
            Closure configure = { configureContext(it) }
    ) {
        new DefaultWebViewComponentContext().tap {
            configure.delegate = it
            configure(it)
        }
    }

    void configureContext(WebViewComponentContext context) {}

}
