package groowt.view.web.lib

import groowt.view.View
import groowt.view.component.ComponentRenderException
import groowt.view.component.context.ComponentContext
import groowt.view.component.context.ComponentScope.TypeAndFactory
import groowt.view.component.factory.ComponentFactory
import groowt.view.web.WebViewComponentChild
import groowt.view.web.util.WithHtml

class IntrinsicHtml extends DelegatingWebViewComponent implements WithHtml {

    static final ComponentFactory<IntrinsicHtml> FACTORY = new IntrinsicHtmlFactory()
    static final TypeAndFactory<IntrinsicHtml> TYPE_AND_FACTORY = new TypeAndFactory<>(IntrinsicHtml, FACTORY)

    private static final Set<String> voidElements = Set.of(
            'area', 'base', 'br', 'col',
            'embed', 'hr', 'img', 'input',
            'link', 'meta', 'param', 'source',
            'track', 'wbr'
    )

    protected static class IntrinsicHtmlFactory implements ComponentFactory<IntrinsicHtml> {

        IntrinsicHtml doCreate(String typeName) {
            new IntrinsicHtml([:], typeName, typeName in voidElements)
        }

        IntrinsicHtml doCreate(String typeName, boolean selfClose) {
            new IntrinsicHtml([:], typeName, selfClose)
        }

        IntrinsicHtml doCreate(String typeName, Map<String, Object> attr) {
            new IntrinsicHtml(attr, typeName, typeName in voidElements)
        }

        IntrinsicHtml doCreate(String typeName, Map<String, Object> attr, boolean selfClose) {
            new IntrinsicHtml(attr, typeName, selfClose)
        }

        IntrinsicHtml doCreate(String typeName, Map<String, Object> attr, List<WebViewComponentChild> children) {
            def intrinsicHtml = new IntrinsicHtml(attr, typeName, typeName in voidElements)
            intrinsicHtml.children = children
            intrinsicHtml
        }

        @Override
        IntrinsicHtml create(String typeName, ComponentContext componentContext, Object... args) {
            return this.doCreate(typeName, componentContext, *args)
        }

        @Override
        IntrinsicHtml create(String alias, Class<?> type, ComponentContext componentContext, Object... args) {
            throw new UnsupportedOperationException('Cannot create an IntrinsicHtml component with a class type.')
        }

    }

    Map attr
    String name
    boolean selfClose

    IntrinsicHtml(Map attr, String elementName, boolean selfClose) {
        this.attr = attr
        this.name = elementName
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
                this.formatAttr(it)
            }
            if (this.selfClose) {
                it << ' /'
            }
            it << '>'
            if (this.hasChildren()) {
                this.children.each {
                    def renderer = it.getRenderer(this)
                    renderer.call(it.child)
                }
            }
            if (this.hasChildren() || !this.selfClose) {
                it << '</'
                it << this.name
                it << '>'
            }
        }
    }

}
