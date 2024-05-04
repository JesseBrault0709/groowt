package groowt.view.web.lib

import groowt.view.StandardGStringTemplateView
import groowt.view.View
import groowt.view.component.ComponentContext
import groowt.view.component.ComponentFactory
import groowt.view.web.WebViewChildComponentRenderer

class Echo extends DelegatingWebViewComponent {

    static final class EchoFactory implements ComponentFactory<Echo> {

        Echo doCreate(String typeName, ComponentContext context, Map<String, Object> attr) {
            doCreate(typeName, context, attr, true)
        }

        Echo doCreate(String typeName, ComponentContext context, Map<String, Object> attr, boolean selfClose) {
            def echo = new Echo(attr, typeName, selfClose)
            echo.context = context
            echo
        }

        Echo doCreate(
                String typeName,
                ComponentContext context,
                Map<String, Object> attr,
                List<WebViewChildComponentRenderer> children
        ) {
            def echo = new Echo(attr, typeName, false)
            echo.context = context
            echo.childRenderers = children
            echo
        }

        @Override
        Echo create(String type, ComponentContext componentContext, Object... args) {
            if (args == null || args.length < 1) {
                throw new IllegalArgumentException(
                        '<Echo> must have at least one attribute. ' +
                                'If you are just echoing children, use a fragment (<>...</>) instead. '
                )
            }
            this.invokeMethod('doCreate', type as String, componentContext, *args) as Echo
        }

        @Override
        Echo create(Class<?> type, ComponentContext componentContext, Object... args) {
            throw new UnsupportedOperationException('<Echo> can only be used with String types.')
        }

    }

    String typeName
    boolean selfClose

    Echo(Map<String, Object> attr, String typeName, boolean selfClose) {
        super(attr)
        this.typeName = typeName
    }

    @Override
    protected View getDelegate() {
        return new StandardGStringTemplateView(
                src: Echo.getResource('EchoTemplate.gst'),
                parent: this
        )
    }

    void renderAttr(Writer out) {
        def iter = this.attr.iterator()
        while (iter.hasNext()) {
            def entry = iter.next()
            out << entry.key
            out << '="'
            out << entry.value
            out << '"'
            if (iter.hasNext()) {
                out << ' '
            }
        }
    }

}
