package groowt.view.web;

import groowt.view.component.ViewComponent;

import java.util.List;

public interface WebViewComponent extends ViewComponent {
    List<WebViewChildRenderer> getChildren();
    void setChildren(List<WebViewChildRenderer> children);
    void renderChildren();
}
