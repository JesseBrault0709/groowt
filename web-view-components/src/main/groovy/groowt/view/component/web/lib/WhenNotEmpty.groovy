package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class WhenNotEmpty extends DelegatingWebViewComponent {

    private final Collection items

    WhenNotEmpty(Map attr) {
        items = attr.items
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            if (!items.empty) {
                def cw = new DefaultComponentWriter(w, context.renderContext, context)
                children.each { cw << it }
            }
        }
    }

}
