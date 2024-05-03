package groowt.view.web;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;

public sealed abstract class WebViewChildRenderer permits WebViewChildComponentRenderer,
        WebViewChildGStringRenderer,
        WebViewChildJStringRenderer {

    private final Closure<Void> renderer;

    public WebViewChildRenderer(Closure<Void> renderer) {
        this.renderer = renderer;
    }

    public void render(ViewComponent parent) {
        this.renderer.setDelegate(parent);
        this.renderer.setResolveStrategy(Closure.DELEGATE_FIRST);
        this.renderer.call();
    }

}
