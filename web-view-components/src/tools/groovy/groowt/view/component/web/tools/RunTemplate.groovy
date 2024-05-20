package groowt.view.component.web.tools

import groowt.view.component.ComponentTemplate
import groowt.view.component.compiler.SimpleComponentTemplateClassFactory
import groowt.view.component.compiler.source.ComponentTemplateSource
import groowt.view.component.web.BaseWebViewComponent
import groowt.view.component.web.DefaultWebViewComponentContext
import groowt.view.component.web.compiler.AnonymousWebViewComponent
import groowt.view.component.web.compiler.DefaultWebViewComponentTemplateCompileUnit
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
    Map<String, String> attr

    static class RunnableTemplate extends BaseWebViewComponent {

        private final Map<String, String> cliAttr

        RunnableTemplate(Class<? extends ComponentTemplate> templateClass, Map<String, String> cliAttr) {
            super(templateClass)
            this.cliAttr = cliAttr
        }

        @Override
        Object getProperty(String propertyName) {
            try {
                return super.getProperty(propertyName)
            } catch (Exception ignored) {
                return this.cliAttr.get(propertyName)
            }
        }

    }

    @Override
    Integer call() throws Exception {
        def compileUnit = new DefaultWebViewComponentTemplateCompileUnit(
                AnonymousWebViewComponent,
                ComponentTemplateSource.of(this.template),
                'groowt.view.web.tools'
        )

        def compileResult = compileUnit.compile()
        def templateLoader = new SimpleComponentTemplateClassFactory()
        def templateClass = templateLoader.getTemplateClass(compileResult)

        def runnableTemplate = new RunnableTemplate(templateClass, this.attr)
        def componentContext = new DefaultWebViewComponentContext()
        runnableTemplate.context = componentContext

        println runnableTemplate.render()

        return 0
    }

    static void main(String[] args) {
        System.exit(new CommandLine(new RunTemplate()).execute(args))
    }

}
