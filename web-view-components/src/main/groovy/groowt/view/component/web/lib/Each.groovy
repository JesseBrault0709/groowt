package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter
import org.jetbrains.annotations.Nullable

class Each extends DelegatingWebViewComponent {

    private final Object items
    private final @Nullable Closure transform

    Each(Map attr) {
        items = attr.items
        transform = attr.transform
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            def cw = new DefaultComponentWriter(w, this.context.renderContext, this.context)
            if (items instanceof Collection) {
                items.each {
                    cw << (transform ? transform(it) : it)
                }
            } else if (items instanceof Map) {
                items.each {
                    cw << (transform ? transform(it) : it)
                }
            } else {
                throw new IllegalArgumentException("The 'items' attribute of Each may only be a Collection or Map.")
            }
        }
    }

}
