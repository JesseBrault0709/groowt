package groowt.view.component.web.lib;

import groowt.view.View;
import groowt.view.component.web.BaseWebViewComponent;

import java.io.IOException;
import java.io.Writer;

public abstract class DelegatingWebViewComponent extends BaseWebViewComponent {

    protected abstract View getDelegate();

    @Override
    public final void renderTo(Writer out) throws IOException {
        this.beforeRender();
        this.getDelegate().renderTo(out);
        this.afterRender();
    }

}
