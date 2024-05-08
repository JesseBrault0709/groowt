package groowt.view.web.tools

import groowt.view.component.compiler.SimpleComponentTemplateClassFactory
import groowt.view.component.compiler.source.ComponentTemplateSource
import groowt.view.component.context.DefaultComponentContext
import groowt.view.component.runtime.DefaultComponentWriter
import groowt.view.web.BaseWebViewComponent
import groowt.view.web.compiler.AnonymousWebViewComponent
import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

import java.util.concurrent.Callable

@Command(
        name = 'runTemplate',
        description = 'render a wvc template with the given params'
)
class RunTemplate implements Callable<Integer> {

    @Parameters(arity = '1', description = 'The template file.')
    File template

    @Option(
            names = ['-A', '--attr', '--attribute'],
            description = 'Attribute(s) to pass to the template.'
    )
    Map<String, String> properties

    static class PropertiesComponent extends BaseWebViewComponent {

        private final Map<String, String> properties

        @SuppressWarnings('GroovyAssignabilityCheck')
        PropertiesComponent(Map<String, Object> attr) {
            super('${renderChildren()}')
            this.properties = attr.properties ?: [:]
        }

        @Override
        Object getProperty(String propertyName) {
            try {
                return super.getProperty(propertyName)
            } catch (Exception ignored) {
                return this.properties.get(propertyName)
            }
        }

    }

    @Override
    Integer call() throws Exception {
        def compileUnit = new WebViewComponentTemplateCompileUnit(
                AnonymousWebViewComponent,
                ComponentTemplateSource.of(this.template),
                'groowt.view.web.tools'
        )

        def compileResult = compileUnit.compile()
        def templateLoader = new SimpleComponentTemplateClassFactory()
        def templateClass = templateLoader.getTemplateClass(compileResult)
        def template = templateClass.getConstructor().newInstance()

        def context = new DefaultComponentContext()
        context.pushDefaultScope()

        def componentWriter = new DefaultComponentWriter(new OutputStreamWriter(System.out))

        template.renderer.call(context, componentWriter)

        return 0
    }

    static void main(String[] args) {
        System.exit(new CommandLine(new RunTemplate()).execute(args))
    }

}
