package groowt.view.web.runtime;

import groovy.lang.GString;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.web.WebViewComponentChild;

import java.util.ArrayList;
import java.util.List;

public class DefaultWebViewComponentChildCollector implements WebViewComponentChildCollector {

    private final ComponentTemplate template;
    private final ComponentWriter out;
    private final List<WebViewComponentChild> children = new ArrayList<>();

    public DefaultWebViewComponentChildCollector(ComponentTemplate template, ComponentWriter out) {
        this.template = template;
        this.out = out;
    }

    @Override
    public void add(String jString) {
        this.children.add(new WebViewComponentChild(this.template, this.out, jString));
    }

    @Override
    public void add(GString gString) {
        this.children.add(new WebViewComponentChild(this.template, this.out, gString));
    }

    @Override
    public void add(ViewComponent component) {
        this.children.add(new WebViewComponentChild(this.template, this.out, component));
    }

    @Override
    public List<WebViewComponentChild> getChildren() {
        return this.children;
    }

}
