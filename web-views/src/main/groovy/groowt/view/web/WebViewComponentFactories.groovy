package groowt.view.web

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import groowt.view.component.factory.ComponentFactory

import java.util.function.Function

final class WebViewComponentFactories {

    static <T extends WebViewComponent> ComponentFactory<T> withAttr(
            @ClosureParams(value = FromString, options = 'java.util.Map<String, Object>')
            Closure<T> closure
    ) {
        ComponentFactory.ofClosure { Map<String, Object> attr -> closure(attr) }
    }

    static <T extends WebViewComponent> ComponentFactory<T> withAttr(Function<Map<String, Object>, T> tFunction) {
        ComponentFactory.ofClosure { Map<String, Object> attr -> tFunction.apply(attr) }
    }

    private WebViewComponentFactories() {}

}
