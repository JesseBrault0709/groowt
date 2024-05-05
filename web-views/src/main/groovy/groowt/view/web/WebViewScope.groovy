package groowt.view.web

import groowt.view.component.factory.ComponentFactory
import groowt.view.component.context.DefaultComponentScope
import groowt.view.web.lib.Echo

class WebViewScope extends DefaultComponentScope {

    @Override
    ComponentFactory factoryMissing(String typeName) {
        Echo.FACTORY
    }

}
