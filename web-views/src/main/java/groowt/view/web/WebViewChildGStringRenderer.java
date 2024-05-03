package groowt.view.web;

import groovy.lang.Closure;
import groovy.lang.GString;

public non-sealed class WebViewChildGStringRenderer extends WebViewChildRenderer {

    private final GString gString;

    public WebViewChildGStringRenderer(GString gString, Closure<Void> renderer) {
        super(renderer);
        this.gString = gString;
    }

    public GString getGString() {
        return this.gString;
    }

    public String getContent() {
        return this.gString.toString();
    }

}
