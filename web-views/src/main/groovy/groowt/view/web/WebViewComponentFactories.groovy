package groowt.view.web

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import groowt.view.component.factory.ComponentFactory
import groowt.view.web.runtime.DefaultWebViewComponentChildCollector
import groowt.view.web.runtime.WebViewComponentChildCollector

import static groowt.view.component.factory.ComponentFactories.ofClosureClassType

final class WebViewComponentFactories {

    static <T extends WebViewComponent> ComponentFactory<T> withAttr(
            Class<T> forClass,
            @ClosureParams(value = FromString, options = 'java.util.Map<String, Object>')
            Closure<? extends T> closure
    ) {
        ofClosureClassType(forClass) { Map<String, Object> attr -> closure(attr) }
    }

    static <T extends WebViewComponent> ComponentFactory<T> withChildren(
            Class<T> forClass,
            @ClosureParams(value = FromString, options = 'java.util.List<groowt.view.web.WebViewComponentChild>')
            Closure<? extends T> closure
    ) {
        ofClosureClassType(forClass) { WebViewComponentChildCollector childCollector ->
            closure(childCollector.children)
        }
    }

    static <T extends WebViewComponent> ComponentFactory<T> withAttrAndChildren(
            Class<T> forClass,
            @ClosureParams(
                    value = FromString,
                    options = 'java.util.Map<String, Object>, java.util.List<groowt.view.web.WebViewComponentChild>'
            )
            Closure<? extends T> closure
    ) {
        ofClosureClassType(forClass) { Map<String, Object> attr, WebViewComponentChildCollector childCollector ->
            closure(attr, childCollector.children)
        }
    }

    private WebViewComponentFactories() {}

}
