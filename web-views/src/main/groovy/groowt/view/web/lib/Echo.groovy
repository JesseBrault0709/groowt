package groowt.view.web.lib

import groowt.view.View
import groowt.view.component.context.ComponentContext
import groowt.view.component.factory.ComponentFactory
import groowt.view.component.ComponentRenderException
import groowt.view.web.WebViewChildComponentRenderer

class Echo extends DelegatingWebViewComponent {

    static final ComponentFactory<Echo> FACTORY = new EchoFactory()

    private static final class EchoFactory implements ComponentFactory<Echo> {

        Echo doCreate(String typeName) {
            doCreate(typeName, [:], true)
        }

        Echo doCreate(String typeName, boolean selfClose) {
            doCreate(typeName, [:], selfClose)
        }

        Echo doCreate(String typeName, Map<String, Object> attr) {
            doCreate(typeName, attr, true)
        }

        Echo doCreate(String typeName, Map<String, Object> attr, boolean selfClose) {
            new Echo(attr, typeName, selfClose)
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
        if (this.selfClose && this.hasChildren()) {
            throw new ComponentRenderException('Cannot have selfClose set to true and have children.')
        }
        return {
            it << '<'
            it << this.name
            if (!this.attr.isEmpty()) {
                it << ' '
                formatAttr(it)
            }
            if (this.selfClose) {
                it << ' /'
            }
            it << '>'
            if (this.hasChildren()) {
                this.renderChildren() // TODO: fix this
            }
            if (this.hasChildren() || !this.selfClose) {
                it << '</'
                it << this.name
                it << '>'
            }
        }
    }

    protected void formatAttr(Writer writer) {
        def iter = this.attr.iterator()
        while (iter.hasNext()) {
            def entry = iter.next()
            writer << entry.key
            writer << '="'
            writer << entry.value
            writer << '"'
            if (iter.hasNext()) {
                writer << ' '
            }
        }
    }

}
