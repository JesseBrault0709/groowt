package groowt.view.web

import groowt.view.component.AbstractViewComponent
import groowt.view.component.ComponentTemplate
import groowt.view.component.factory.ComponentTemplateSource

class DefaultWebViewComponent extends AbstractWebViewComponent {

    DefaultWebViewComponent() {}

    DefaultWebViewComponent(ComponentTemplate template) {
        super(template)
    }

    DefaultWebViewComponent(Class<? extends ComponentTemplate> templateType) {
        super(templateType)
    }

    DefaultWebViewComponent(ComponentTemplateSource source) {
        super(source)
    }

    DefaultWebViewComponent(ComponentTemplateSource source, WebViewComponentTemplateCompiler compiler) {
        super(source, compiler)
    }

    /**
     * A convenience constructor which creates a {@link ComponentTemplateSource}
     * from the given {@code source} parameter and passes it to super. See
     * {@link ComponentTemplateSource} for possible types.
     *
     * @param source the object passed to {@link ComponentTemplateSource#of}
     *
     * @see ComponentTemplateSource
     */
    @SuppressWarnings('GroovyAssignabilityCheck')
    DefaultWebViewComponent(Object source) {
        super(ComponentTemplateSource.of(source))
    }

    @Override
    protected final Class<? extends AbstractViewComponent> getSelfClass() {
        this.class
    }

}
