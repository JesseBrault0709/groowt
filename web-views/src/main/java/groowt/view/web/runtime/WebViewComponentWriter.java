package groowt.view.web.runtime;

import groovy.lang.GString;
import groowt.view.component.ViewComponent;

public interface WebViewComponentWriter {
    void append(String string);
    void append(GString gString);
    void append(GString gString, int line, int column);
    void append(ViewComponent viewComponent);
    void append(ViewComponent viewComponent, int line, int column);
    void append(Object object);
    void leftShift(Object object);
}
