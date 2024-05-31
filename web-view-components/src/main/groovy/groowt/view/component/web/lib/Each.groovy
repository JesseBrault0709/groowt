package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter
import org.jetbrains.annotations.Nullable

class Each extends DelegatingWebViewComponent {

    private final Collection items
    private final @Nullable Closure transform

    Each(Map attr) {
        items = attr.items
        transform = attr.transform
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            def cw = new DefaultComponentWriter(w, this.context.renderContext, this.context)
            items.forEach {
                cw << (transform ? transform(it) : it)
            }
        }
    }

}
