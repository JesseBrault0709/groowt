package groowt.view.web

import groowt.view.component.AbstractViewComponent
import groowt.view.component.ComponentTemplate
import groowt.view.component.TemplateSource

class DefaultWebViewComponent extends AbstractWebViewComponent {

    DefaultWebViewComponent() {}

    DefaultWebViewComponent(ComponentTemplate template) {
        super(template)
    }

    DefaultWebViewComponent(Class<? extends ComponentTemplate> templateType) {
        super(templateType)
    }

    DefaultWebViewComponent(TemplateSource source) {
        super(source)
    }

    DefaultWebViewComponent(TemplateSource source, WebViewComponentTemplateCompiler compiler) {
        super(source, compiler)
    }

    /**
     * A convenience constructor which creates a {@link TemplateSource}
     * from the given {@code source} parameter and passes it to super. See
     * {@link TemplateSource} for possible types.
     *
     * @param source the object passed to {@link TemplateSource#of}
     *
     * @see TemplateSource
     */
    @SuppressWarnings('GroovyAssignabilityCheck')
    DefaultWebViewComponent(Object source) {
        super(TemplateSource.of(source))
    }

    @Override
    protected final Class<? extends AbstractViewComponent> getSelfClass() {
        this.class
    }

}
