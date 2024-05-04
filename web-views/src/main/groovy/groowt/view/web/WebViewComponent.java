package groowt.view.web;

import groowt.view.component.ViewComponent;

import java.util.List;

public interface WebViewComponent extends ViewComponent {

    List<WebViewChildRenderer> getChildRenderers();
    boolean hasChildren();
    void setChildRenderers(List<WebViewChildRenderer> children);
    void renderChildren();

    default List<Object> getChildren() {
        return this.getChildRenderers().stream()
                .map(childRenderer -> switch (childRenderer) {
                    case WebViewChildComponentRenderer componentRenderer -> componentRenderer.getComponent();
                    case WebViewChildGStringRenderer gStringRenderer -> gStringRenderer.getGString();
                    case WebViewChildJStringRenderer jStringRenderer -> jStringRenderer.getContent();
                })
                .toList();
    }

}
