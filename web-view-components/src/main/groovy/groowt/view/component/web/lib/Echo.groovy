package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.DefaultComponentWriter

class Echo extends DelegatingWebViewComponent {

    Map attr

    Echo(Map attr) {
        this.attr = attr
    }

    @Override
    Object getProperty(String propertyName) {
        try {
            return super.getProperty(propertyName)
        } catch (MissingPropertyException missingPropertyException) {
            if (this.attr.containsKey(propertyName)) {
                return this.attr[propertyName]
            } else {
                throw missingPropertyException
            }
        }
    }

    @Override
    protected View getDelegate() {
        return {
            def componentWriter = new DefaultComponentWriter(it)
            componentWriter.setComponentContext(this.context)
            componentWriter.setRenderContext(this.context.renderContext) // hacky
            this.children.each {
                componentWriter << it
            }
        }
    }

}
