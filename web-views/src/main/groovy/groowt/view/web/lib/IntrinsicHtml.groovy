package groowt.view.web.lib

import groowt.view.component.context.ComponentContext
import groowt.view.component.factory.ComponentFactory
import groowt.view.web.WebViewChildComponentRenderer

class IntrinsicHtml extends Echo {

    // TODO: check type name for HTML 5 validity
    protected static class IntrinsicHtmlFactory implements ComponentFactory<IntrinsicHtml> {

        IntrinsicHtml doCreate(String typeName) {
            new IntrinsicHtml([:], typeName, false)
        }

        IntrinsicHtml doCreate(String typeName, boolean selfClose) {
            new IntrinsicHtml([:], typeName, selfClose)
        }

        IntrinsicHtml doCreate(String typeName, Map<String, Object> attr) {
            new IntrinsicHtml(attr, typeName, false)
        }

        IntrinsicHtml doCreate(String typeName, Map<String, Object> attr, boolean selfClose) {
            new IntrinsicHtml(attr, typeName, selfClose)
        }

        IntrinsicHtml doCreate(String typeName, Map<String, Object> attr, List<WebViewChildComponentRenderer> children) {
            def intrinsicHtml = new IntrinsicHtml(attr, typeName, false)
            intrinsicHtml.childRenderers = children
            intrinsicHtml
        }

        @Override
        IntrinsicHtml create(String typeName, ComponentContext componentContext, Object... args) {
            return this.doCreate(typeName, componentContext, *args)
        }

        @Override
        IntrinsicHtml create(Class<?> type, ComponentContext componentContext, Object... args) {
            throw new UnsupportedOperationException('Cannot create an IntrinsicHtml component with a class type.')
        }

    }

    IntrinsicHtml(Map<String, Object> attr, String elementName, boolean selfClose) {
        super(attr, elementName, selfClose)
    }

}
