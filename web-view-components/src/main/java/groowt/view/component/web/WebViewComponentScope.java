package groowt.view.component.web;

import groowt.view.component.context.ComponentScope;

public interface WebViewComponentScope extends ComponentScope {
    <T extends WebViewComponent> void addWithAttr(Class<T> componentClass);
}
