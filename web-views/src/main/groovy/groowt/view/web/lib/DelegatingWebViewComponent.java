package groowt.view.web.lib;

import groowt.view.View;
import groowt.view.web.DefaultWebViewComponent;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public abstract class DelegatingWebViewComponent extends DefaultWebViewComponent {

    private final Map<String, Object> attr;

    public DelegatingWebViewComponent(Map<String, Object> attr) {
        this.attr = attr;
    }

    protected Map<String, Object> getAttr() {
        return this.attr;
    }

    protected abstract View getDelegate();

    @Override
    public final void renderTo(Writer out) throws IOException {
        this.beforeRender();
        this.getDelegate().renderTo(out);
        this.afterRender();
    }

}
