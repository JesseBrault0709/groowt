package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class Outlet extends DelegatingWebViewComponent {

    private final List givenChildren

    Outlet(Map attr) {
        givenChildren = attr.children ?: []
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            def cw = new DefaultComponentWriter(w, context.renderContext, context)
            givenChildren.each { cw << it }
        }
    }

}
