package groowt.view.component.web.lib

import groowt.view.View

class Render extends DelegatingWebViewComponent {

    private final Object item

    Render(Map attr) {
        item = Objects.requireNonNull(attr.item, "<Render> attribute 'item' must not be null.")
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            if (item.respondsTo('renderTo', [Writer] as Class[])) {
                item.renderTo(w)
            } else if (item.respondsTo('render')) {
                w << item.render()
            } else {
                throw new IllegalArgumentException(
                        '<Render> must use an item which responds to either renderTo(Writer) or render().'
                )
            }
        }
    }

}
