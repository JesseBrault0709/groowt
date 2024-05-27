package groowt.view.component.web.lib

import groowt.view.View
import groowt.view.component.runtime.ComponentWriter
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
                def value = this.attr[propertyName]
                if (value instanceof Closure) {
                    if (value.maximumNumberOfParameters == 0) {
                        return value
                    } else {
                        return value()
                    }
                } else {
                    return value
                }
            } else {
                throw missingPropertyException
            }
        }
    }

    @Override
    protected View getDelegate() {
        return { Writer w ->
            def componentWriter = new DefaultComponentWriter(w)
            componentWriter.setComponentContext(this.context)
            componentWriter.setRenderContext(this.context.renderContext)
            this.children.each {
                if (it instanceof Closure) {
                    if (it.maximumNumberOfParameters == 1 && ComponentWriter.isAssignableFrom(it.parameterTypes[0])) {
                        it(componentWriter)
                    } else {
                        componentWriter << it()
                    }
                } else {
                    componentWriter << it
                }
            }
        }
    }

}
