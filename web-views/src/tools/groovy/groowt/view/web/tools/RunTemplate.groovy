package groowt.view.web.tools

import groowt.view.component.DefaultComponentContext
import groowt.view.web.DefaultWebComponentTemplateCompiler
import groowt.view.web.DefaultWebViewComponent
import groowt.view.web.WebViewTemplateComponentSource
import groowt.view.web.runtime.WebViewComponentWriter
import org.codehaus.groovy.control.CompilerConfiguration
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

    @Override
    Integer call() throws Exception {
        def component = new DefaultWebViewComponent(WebViewTemplateComponentSource.of(this.template))

        def context = new DefaultComponentContext()
        context.pushDefaultScope()
        component.context = context

        println component.render()

        return 0
    }

    static void main(String[] args) {
        System.exit(new CommandLine(new RunTemplate()).execute(args))
    }

}
