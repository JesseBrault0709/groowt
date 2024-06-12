package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class Switch extends DelegatingWebViewComponent {

    final Object item
    boolean doneYet

    Switch(Map attr) {
        item = attr.item
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            def cw = new DefaultComponentWriter(w, context.renderContext, context)
            children.each { cw << it }
        }
    }

}
