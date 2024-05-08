package groowt.view.web

import groowt.view.component.context.ComponentScope
import groowt.view.component.context.DefaultComponentContext

class DefaultWebViewComponentContext extends DefaultComponentContext implements WebViewComponentContext {

    DefaultWebViewComponentContext() {
        this.pushScope(WebViewComponentScope.getDefaultRootScope())
    }

    @Override
    protected ComponentScope getNewDefaultScope() {
        new WebViewComponentScope()
    }

}
