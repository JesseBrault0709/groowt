package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class WhenNotNull extends DelegatingWebViewComponent {

    private final Object item
    private final Closure render

    WhenNotNull(Map attr) {
        this.item = attr.item
        this.render = attr.render ?: Closure.IDENTITY
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            if (this.item != null) {
                def cw = new DefaultComponentWriter(w)
                cw.renderContext = this.context.renderContext
                cw.componentContext = this.context
                cw << this.render(item)
            }
        }
    }

}
