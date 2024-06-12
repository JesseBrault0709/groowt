package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class WhenNotEmpty extends DelegatingWebViewComponent {

    private final Object items

    WhenNotEmpty(Map attr) {
        items = attr.items
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            if (items instanceof Collection && !items.empty || items instanceof Map && !items.isEmpty()) {
                def cw = new DefaultComponentWriter(w, context.renderContext, context)
                children.each { cw << it }
            }
        }
    }

}
