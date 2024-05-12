package groowt.view.web.lib

import groowt.view.web.BaseWebViewComponent

final class Fragment extends BaseWebViewComponent {

    @Override
    void renderTo(Writer out) throws IOException {
        this.beforeRender()
        this.renderChildren(out)
        this.afterRender()
    }

}
