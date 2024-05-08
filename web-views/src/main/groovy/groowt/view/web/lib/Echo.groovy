package groowt.view.web.lib

import groowt.view.View
import groowt.view.component.context.ComponentContext
import groowt.view.component.factory.ComponentFactory
import groowt.view.web.runtime.WebViewComponentChildCollector

class Echo extends DelegatingWebViewComponent {

    static final ComponentFactory<Echo> FACTORY = new EchoFactory()

    protected static class EchoFactory implements ComponentFactory<Echo> {

        protected Echo doCreate() {
            new Echo([:], [])
        }

        protected Echo doCreate(Map attr) {
            new Echo(attr, [])
        }

        protected Echo doCreate(WebViewComponentChildCollector childCollector) {
            new Echo([:], childCollector.children)
        }

        protected Echo doCreate(Map attr, WebViewComponentChildCollector childCollector) {
            new Echo(attr, childCollector.children)
        }

        @Override
        Echo create(String typeName, ComponentContext componentContext, Object... args) {
            throw new UnsupportedOperationException('Cannot create Echo for string type components')
        }

        @Override
        Echo create(String alias, Class<?> type, ComponentContext componentContext, Object... args) {
            this.doCreate(*args)
        }

    }

    Map attr

    Echo(Map attr, List children) {
        this.attr = attr
        this.children = children
    }

    Object propertyMissing(String propertyName) {
        attr[propertyName]
    }

    @Override
    protected View getDelegate() {
        return {
            this.children.each {
                it.render(this)
            }
        }
    }

}
