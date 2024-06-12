package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class DefaultCase extends DelegatingWebViewComponent {

    @Override
    protected View getDelegate() {
        return { Writer w ->
            def parent = context.getParent(Switch)
            if (!parent.doneYet) {
                parent.doneYet = true
                def cw = new DefaultComponentWriter(w, context.renderContext, context)
                children.each { cw << it }
            }
        }
    }

}
