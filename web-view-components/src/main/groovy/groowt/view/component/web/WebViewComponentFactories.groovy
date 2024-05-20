package groowt.view.component.web

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import groowt.view.component.factory.ComponentFactory

import static groowt.view.component.factory.ComponentFactories.ofClosureClassType

final class WebViewComponentFactories {

    static <T extends WebViewComponent> ComponentFactory<T> withAttr(
            Class<T> forClass,
            @ClosureParams(value = FromString, options = 'java.util.Map<String, Object>')
            Closure<? extends T> closure
    ) {
        ofClosureClassType(forClass) { Map<String, Object> attr, Object[] ignored -> closure(attr) }
    }

    private WebViewComponentFactories() {}

}
