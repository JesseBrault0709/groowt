package groowt.view.web.lib

import groowt.view.web.DefaultWebViewComponent

class Fragment extends DefaultWebViewComponent {

    @Override
    void renderTo(Writer out) throws IOException {
        this.beforeRender()
        this.renderChildren()
        this.afterRender()
    }

}
