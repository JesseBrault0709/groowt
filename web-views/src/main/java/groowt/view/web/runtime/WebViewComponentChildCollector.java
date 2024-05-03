package groowt.view.web.runtime;

import groovy.lang.Closure;
import groovy.lang.GString;
import groowt.view.component.ViewComponent;
import groowt.view.web.WebViewChildComponentRenderer;
import groowt.view.web.WebViewChildGStringRenderer;
import groowt.view.web.WebViewChildJStringRenderer;
import groowt.view.web.WebViewChildRenderer;

import java.util.ArrayList;
import java.util.List;

public class WebViewComponentChildCollector {

    private final List<WebViewChildRenderer> children = new ArrayList<>();

    public void add(String jString, Closure<Void> renderer) {
        this.children.add(new WebViewChildJStringRenderer(jString, renderer));
    }

    public void add(GString gString, Closure<Void> renderer) {
        this.children.add(new WebViewChildGStringRenderer(gString, renderer));
    }

    public void add(ViewComponent component, Closure<Void> renderer) {
        this.children.add(new WebViewChildComponentRenderer(component, renderer));
    }

    public List<WebViewChildRenderer> getChildren() {
        return this.children;
    }

}
