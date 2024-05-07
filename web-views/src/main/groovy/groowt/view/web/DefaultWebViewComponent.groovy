package groowt.view.web

import groowt.view.component.AbstractViewComponent
import groowt.view.component.ComponentTemplate
import groowt.view.component.compiler.ComponentTemplateCompiler
import groowt.view.component.factory.ComponentTemplateSource
import groowt.view.web.compiler.DefaultWebViewComponentTemplateCompiler
import groowt.view.web.compiler.WebViewComponentTemplateCompiler
import org.codehaus.groovy.control.CompilerConfiguration

import java.util.function.Function

abstract class DefaultWebViewComponent extends AbstractWebViewComponent {

    private static final GroovyClassLoader groovyClassLoader =
            new GroovyClassLoader(DefaultWebViewComponent.classLoader)

    private static final Function<Class, ComponentTemplateCompiler> compilerFunction = { Class givenSelfClass ->
        new DefaultWebViewComponentTemplateCompiler(
                groovyClassLoader,
                CompilerConfiguration.DEFAULT,
                givenSelfClass.packageName
        )
    }

    protected DefaultWebViewComponent() {}

    protected DefaultWebViewComponent(ComponentTemplate template) {
        super(template)
    }

    protected DefaultWebViewComponent(Class<? extends ComponentTemplate> templateType) {
        super(templateType)
    }

    protected DefaultWebViewComponent(ComponentTemplateSource source) {
        super(source, compilerFunction)
    }

    protected DefaultWebViewComponent(ComponentTemplateSource source, WebViewComponentTemplateCompiler compiler) {
        super(source, compiler)
    }

    /**
     * A convenience constructor which creates a {@link ComponentTemplateSource}
     * from the given {@code source} parameter and passes it to super. See
     * {@link ComponentTemplateSource} for possible types.
     *
     * @param source the object passed to {@link ComponentTemplateSource#of}
     * @param compiler the compiler to use
     *
     * @see ComponentTemplateSource
     */
    @SuppressWarnings('GroovyAssignabilityCheck')
    protected DefaultWebViewComponent(Object source, WebViewComponentTemplateCompiler compiler) {
        super(ComponentTemplateSource.of(source), compiler)
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
    protected DefaultWebViewComponent(Object source) {
        super(ComponentTemplateSource.of(source), compilerFunction)
    }

    @Override
    protected final Class<? extends AbstractViewComponent> getSelfClass() {
        this.class
    }

}
