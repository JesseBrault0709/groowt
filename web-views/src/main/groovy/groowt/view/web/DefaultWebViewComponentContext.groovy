package groowt.view.web

import groowt.view.component.ComponentScope
import groowt.view.component.DefaultComponentContext
import groowt.view.component.ViewComponent
import groowt.view.web.lib.Fragment
import groowt.view.web.runtime.WebViewComponentChildCollector
import org.jetbrains.annotations.ApiStatus

class DefaultWebViewComponentContext extends DefaultComponentContext {

    @Override
    protected ComponentScope getNewDefaultScope() {
        new WebViewScope()
    }

    @ApiStatus.Internal
    ViewComponent createFragment(Closure<?> childCollector) {
        def collector = new WebViewComponentChildCollector()
        childCollector.call(collector)
        def fragment = new Fragment()
        fragment.childRenderers = collector.children
        fragment
    }

}
