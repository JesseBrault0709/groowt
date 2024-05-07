package groowt.view.web.tools

import groowt.view.component.context.DefaultComponentContext
import groowt.view.component.factory.ComponentTemplateSource
import groowt.view.web.compiler.DefaultWebViewComponentTemplateCompiler
import groowt.view.web.runtime.DefaultWebViewComponentWriter
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
        def gcl = new GroovyClassLoader(this.class.classLoader)
        def compiler = new DefaultWebViewComponentTemplateCompiler(
                gcl,
                CompilerConfiguration.DEFAULT,
                'groowt.view.web.tools'
        )
        def template = compiler.compileAndGetAnonymous(ComponentTemplateSource.of(this.template))

        def context = new DefaultComponentContext()
        context.pushDefaultScope()

        def componentWriter = new DefaultWebViewComponentWriter(new OutputStreamWriter(System.out))

        template.renderer.call(context, componentWriter)

        return 0
    }

    static void main(String[] args) {
        System.exit(new CommandLine(new RunTemplate()).execute(args))
    }

}
