package groowt.view.web

import groowt.view.component.ComponentFactory
import groowt.view.component.DefaultComponentScope
import groowt.view.web.lib.Echo.EchoFactory

class WebViewScope extends DefaultComponentScope {

    private final EchoFactory echoFactory = new EchoFactory()

    @Override
    ComponentFactory factoryMissing(String typeName) {
        echoFactory
    }

}
