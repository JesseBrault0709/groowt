package groowt.view.component.web

import groowt.view.component.context.DefaultComponentScope
import groowt.view.component.web.lib.Echo
import groowt.view.component.web.lib.IntrinsicHtml
import org.codehaus.groovy.runtime.InvokerHelper

import static WebViewComponentFactories.withAttr

class DefaultWebViewComponentScope extends DefaultComponentScope implements WebViewComponentScope {

    static DefaultWebViewComponentScope getDefaultRootScope() {
        new DefaultWebViewComponentScope().tap {
            addWithAttr(Echo)
        }
    }

    @Override
    <T extends WebViewComponent> void addWithAttr(Class<T> componentClass) {
        add(componentClass, withAttr(componentClass) { attr ->
            InvokerHelper.invokeConstructorOf(componentClass, attr) as T
        })
    }

    @Override
    TypeAndFactory factoryMissing(String typeName) {
        IntrinsicHtml.TYPE_AND_FACTORY
    }

}
