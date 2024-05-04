package groowt.view.web.lib

import groowt.view.StandardGStringTemplateView
import groowt.view.View
import groowt.view.component.ComponentContext
import groowt.view.component.ComponentFactory
import groowt.view.web.WebViewChildComponentRenderer

class Echo extends DelegatingWebViewComponent {

    static final class EchoFactory implements ComponentFactory<Echo> {

        Echo doCreate(String typeName, boolean selfClose) {
            doCreate(typeName, [:], selfClose)
        }

        Echo doCreate(String typeName, Map<String, Object> attr) {
            doCreate(typeName, attr, true)
        }

        Echo doCreate(String typeName, Map<String, Object> attr, boolean selfClose) {
            def echo = new Echo(attr, typeName, selfClose)
            echo
        }

        Echo doCreate(
                String typeName,
                Map<String, Object> attr,
                List<WebViewChildComponentRenderer> children
        ) {
            def echo = new Echo(attr, typeName, false)
            echo.childRenderers = children
            echo
        }

        @Override
        Echo create(String type, ComponentContext componentContext, Object... args) {
            this.doCreate(type, *args) as Echo
        }

        @Override
        Echo create(Class<?> type, ComponentContext componentContext, Object... args) {
            throw new UnsupportedOperationException('<Echo> can only be used with String types.')
        }

    }

    String name
    boolean selfClose

    Echo(Map<String, Object> attr, String name, boolean selfClose) {
        super(attr)
        this.name = name
        this.selfClose = selfClose
    }

    @Override
    protected View getDelegate() {
        return new StandardGStringTemplateView(
                src: Echo.getResource('EchoTemplate.gst'),
                parent: this
        )
    }

    String formatAttr() {
        def sb = new StringBuilder()
        def iter = this.attr.iterator()
        while (iter.hasNext()) {
            def entry = iter.next()
            sb << entry.key
            sb << '="'
            sb << entry.value
            sb << '"'
            if (iter.hasNext()) {
                sb << ' '
            }
        }
        sb.toString()
    }

}
