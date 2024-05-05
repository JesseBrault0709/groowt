package groowt.view.web

import groowt.view.component.context.ComponentScope
import groowt.view.component.context.DefaultComponentContext
import groowt.view.component.ViewComponent
import groowt.view.web.lib.Fragment
import groowt.view.web.runtime.DefaultWebViewComponentChildCollection
import org.jetbrains.annotations.ApiStatus

class DefaultWebViewComponentContext extends DefaultComponentContext implements WebViewComponentContext {

    @Override
    protected ComponentScope getNewDefaultScope() {
        new WebViewScope()
    }

    @Override
    @ApiStatus.Internal
    ViewComponent createFragment(Closure<?> childCollector) {
        def childCollection = new DefaultWebViewComponentChildCollection()
        childCollector.call(childCollection)
        def fragment = new Fragment()
        fragment.childRenderers = childCollection.children
        fragment
    }

}
