package groowt.view.web.lib

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
        } catch (MissingPropertyException ignored) {
            return attr[propertyName]
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
