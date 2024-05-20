package groowt.view.web;

import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.ComponentWriter;

import java.io.Writer;
import java.util.List;

public interface WebViewComponent extends ViewComponent {

    List<Object> getChildren();
    boolean hasChildren();
    void setChildren(List<?> children);
    void renderChildren();
    void renderChildren(Writer to);
    void renderChildren(ComponentWriter to);

}
