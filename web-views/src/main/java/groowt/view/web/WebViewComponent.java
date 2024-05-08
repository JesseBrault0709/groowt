package groowt.view.web;

import groovy.lang.GString;
import groowt.view.component.ViewComponent;

import java.util.List;

public interface WebViewComponent extends ViewComponent {

    List<WebViewComponentChild> getChildren();
    boolean hasChildren();
    void setChildren(List<WebViewComponentChild> children);
    void renderChildren();

    default List<String> getChildStrings() {
        return this.getChildren().stream()
                .map(WebViewComponentChild::getChild)
                .filter(obj -> obj instanceof String || obj instanceof GString)
                .map(obj -> {
                    if (obj instanceof String s) {
                        return s;
                    } else {
                        return ((GString) obj).toString();
                    }
                })
                .toList();
    }

    default List<GString> getChildGStrings() {
        return this.getChildren().stream()
                .map(WebViewComponentChild::getChild)
                .filter(GString.class::isInstance)
                .map(GString.class::cast)
                .toList();
    }

    default List<WebViewComponent> getChildComponents() {
        return this.getChildren().stream()
                .map(WebViewComponentChild::getChild)
                .filter(WebViewComponent.class::isInstance)
                .map(WebViewComponent.class::cast)
                .toList();
    }

}
