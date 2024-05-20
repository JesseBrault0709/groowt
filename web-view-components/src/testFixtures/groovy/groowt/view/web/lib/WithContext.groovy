package groowt.view.web.lib

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groowt.view.web.DefaultWebViewComponentContext
import groowt.view.web.WebViewComponentContext

trait WithContext {

    WebViewComponentContext context(
            @ClosureParams(value = SimpleType, options = 'groowt.view.web.WebViewComponentContext')
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
