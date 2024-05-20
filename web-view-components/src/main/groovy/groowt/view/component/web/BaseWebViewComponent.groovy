package groowt.view.component.web

import groowt.view.component.AbstractViewComponent
import groowt.view.component.ComponentTemplate
import groowt.view.component.compiler.ComponentTemplateCompileUnit
import groowt.view.component.compiler.source.ComponentTemplateSource

import java.util.function.Function

abstract class BaseWebViewComponent extends AbstractWebViewComponent {

    BaseWebViewComponent() {}

    BaseWebViewComponent(ComponentTemplate template) {
        super(template)
    }

    BaseWebViewComponent(Class<? extends ComponentTemplate> templateClass) {
        super(templateClass)
    }

    BaseWebViewComponent(
            Function<? super Class<? extends AbstractViewComponent>, ComponentTemplateCompileUnit> compileUnitFunction
    ) {
        super(compileUnitFunction)
    }

    BaseWebViewComponent(ComponentTemplateSource source) {
        super(source)
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
    BaseWebViewComponent(Object source) {
        super(ComponentTemplateSource.of(source))
    }

}
