package groowt.view.component.web.lib

import groowt.view.component.web.BaseWebViewComponent

final class Fragment extends BaseWebViewComponent {

    @Override
    void renderTo(Writer out) throws IOException {
        this.beforeRender()
        this.renderChildren(out)
        this.afterRender()
    }

}
