package groowt.view.web.runtime;

import groovy.lang.GString;
import groowt.view.component.ViewComponent;
import groowt.view.web.WebViewComponentChild;

import java.util.List;

public interface WebViewComponentChildCollector {
    void add(String jString);
    void add(GString gString);
    void add(ViewComponent component);
    List<WebViewComponentChild> getChildren();
}
