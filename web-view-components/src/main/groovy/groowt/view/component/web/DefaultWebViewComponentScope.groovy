package groowt.view.component.web

import groowt.view.component.context.DefaultComponentScope
import groowt.view.component.web.lib.*
import org.codehaus.groovy.runtime.InvokerHelper

import static WebViewComponentFactories.withAttr

class DefaultWebViewComponentScope extends DefaultComponentScope implements WebViewComponentScope {

    static DefaultWebViewComponentScope getDefaultRootScope() {
        new DefaultWebViewComponentScope().tap {
            addWithAttr(Case)
            addWithAttr(DefaultCase)
            addWithAttr(Each)
            addWithAttr(Echo)
            addWithAttr(Outlet)
            addWithAttr(Render)
            addWithAttr(Switch)
            addWithAttr(WhenNotEmpty)
            addWithAttr(WhenNotNull)
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
