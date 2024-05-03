package groowt.view.web;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;

public non-sealed class WebViewChildComponentRenderer extends WebViewChildRenderer {

    private final ViewComponent component;

    public WebViewChildComponentRenderer(ViewComponent component, Closure<Void> renderer) {
        super(renderer);
        this.component = component;
    }

    public ViewComponent getComponent() {
        return this.component;
    }

}
