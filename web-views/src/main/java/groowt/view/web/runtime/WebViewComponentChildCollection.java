package groowt.view.web.runtime;

import groovy.lang.Closure;
import groovy.lang.GString;
import groowt.view.component.ViewComponent;
import groowt.view.web.WebViewChildRenderer;

import java.util.List;

public interface WebViewComponentChildCollection {

    void add(String jString, Closure<Void> renderer);

    void add(GString gString, Closure<Void> renderer);

    void add(ViewComponent component, Closure<Void> renderer);

    List<WebViewChildRenderer> getChildren();

}
