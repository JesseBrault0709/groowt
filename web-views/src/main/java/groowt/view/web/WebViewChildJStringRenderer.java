package groowt.view.web;

import groovy.lang.Closure;

public non-sealed class WebViewChildJStringRenderer extends WebViewChildRenderer {

    private final String content;

    public WebViewChildJStringRenderer(String content, Closure<Void> renderer) {
        super(renderer);
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

}
