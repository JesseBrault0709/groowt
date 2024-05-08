package groowt.view.web

import groowt.view.component.context.DefaultComponentScope
import groowt.view.web.lib.Echo
import groowt.view.web.lib.IntrinsicHtml
import org.codehaus.groovy.runtime.InvokerHelper

import static groowt.view.web.WebViewComponentFactories.*

class WebViewComponentScope extends DefaultComponentScope {

    static WebViewComponentScope getDefaultRootScope() {
        new WebViewComponentScope().tap {
            add(Echo, Echo.FACTORY)
        }
    }

    <T extends WebViewComponent> void addWithAttr(Class<T> componentClass) {
        add(componentClass, withAttr(componentClass) { attr ->
            InvokerHelper.invokeConstructorOf(componentClass, attr) as T
        })
    }

    <T extends WebViewComponent> void addWithChildren(Class<T> componentClass) {
        add(componentClass, withChildren(componentClass) { children ->
            InvokerHelper.invokeConstructorOf(componentClass, children) as T
        })
    }

    <T extends WebViewComponent> void addWithAttrAndChildren(Class<T> componentClass) {
        add(componentClass, withAttrAndChildren(componentClass) { attr, children ->
            InvokerHelper.invokeConstructorOf(componentClass, [attr, children] as Object[]) as T
        })
    }

    @Override
    TypeAndFactory factoryMissing(String typeName) {
        IntrinsicHtml.TYPE_AND_FACTORY
    }

}
