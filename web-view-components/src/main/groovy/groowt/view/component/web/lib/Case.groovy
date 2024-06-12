package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class Case extends DelegatingWebViewComponent {

    private final Object match

    Case(Map attr) {
        match = attr.match
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            def parent = context.getParent(Switch)
            if (!parent.doneYet && match == parent.item) {
                parent.doneYet = true
                def cw = new DefaultComponentWriter(w, context.renderContext, context)
                children.each { cw << it }
            }
        }
    }

}
