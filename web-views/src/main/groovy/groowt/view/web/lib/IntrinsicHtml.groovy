package groowt.view.web.lib

import groowt.view.View
import groowt.view.component.ComponentRenderException
import groowt.view.component.context.ComponentContext
import groowt.view.component.context.ComponentScope.TypeAndFactory
import groowt.view.component.factory.ComponentFactory
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

        IntrinsicHtml doCreate(String typeName, Map<String, Object> attr) {
            new IntrinsicHtml(attr, typeName, typeName in voidElements)
        }

        @Override
        IntrinsicHtml create(String typeName, ComponentContext componentContext, Object... args) {
            return this.doCreate(typeName, *args)
        }

        @Override
        IntrinsicHtml create(String alias, Class<?> type, ComponentContext componentContext, Object... args) {
            throw new UnsupportedOperationException('Cannot create an IntrinsicHtml component with a class type.')
        }

    }

    Map attr
    String name
    boolean isVoidElement

    IntrinsicHtml(Map attr, String elementName, boolean isVoidElement) {
        this.attr = attr
        this.name = elementName
        this.isVoidElement = isVoidElement
    }

    @Override
    protected View getDelegate() {
        if (this.isVoidElement && this.hasChildren()) {
            throw new ComponentRenderException('A void html element cannot have children.')
        }
        return { writer ->
            writer << '<'
            writer << this.name
            if (!this.attr.isEmpty()) {
                writer << ' '
                this.formatAttr(writer)
            }
            writer << '>'
            if (this.hasChildren()) {
                this.children.each {
                    it.renderTo(writer, this)
                }
            }
            if (!this.isVoidElement) {
                writer << '</'
                writer << this.name
                writer << '>'
            }
        }
    }

}
