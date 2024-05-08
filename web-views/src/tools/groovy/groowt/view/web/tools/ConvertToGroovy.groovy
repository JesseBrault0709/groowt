package groowt.view.web.tools

import groovy.console.ui.AstNodeToScriptVisitor
import groowt.view.component.compiler.source.ComponentTemplateSource
import groowt.view.web.compiler.AnonymousWebViewComponent
import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit
import org.codehaus.groovy.tools.GroovyClass
import picocli.CommandLine

import java.util.concurrent.Callable

@CommandLine.Command(
        name = 'convertToGroovy',
        description = 'Convert a given wvc source file to groovy source code.',
        mixinStandardHelpOptions = true
)
class ConvertToGroovy implements Callable<Integer> {

    static void main(String[] args) {
        System.exit(new CommandLine(new ConvertToGroovy()).execute(args))
    }

    @CommandLine.Parameters(arity = '1..*', description = 'The source files to convert.')
    List<File> targets

    @CommandLine.Option(names = ['-p', '--package'], description = 'The default package name for the targets.')
    String defaultPackageName

    @CommandLine.Option(names = ['-o', '--out'], description = 'Write source files to disk instead of printing them.')
    boolean writeOut

    @CommandLine.Option(names = ['-q', '--quiet'], description = 'Do not print the class source to the console.')
    boolean quiet

    // Default is Phases.CLASS_GENERATION (7)
    @CommandLine.Option(
            names = ['-t', '--compilePhase'],
            defaultValue = '7',
            description = 'The groovy compile phase to target.'
    )
    int compilePhase

    @CommandLine.Option(
            names = ['-c', '--classes'],
            description = 'Whether to output the class files, if the compile phase is late enough. If this is false, any generated classes will be thrown out.'
    )
    boolean doClasses

    @CommandLine.Option(
            names = ['-d', '--classesDir'],
            description = 'If the GroovyCompiler outputs classes, where to write them.'
    )
    File classesDir

    private void writeClass(File classesDir, GroovyClass groovyClass) {
        new File(classesDir, groovyClass.name + '.class').withOutputStream {
            it.write(groovyClass.bytes)
        }
    }

    @Override
    Integer call() throws Exception {
        boolean success = this.targets.inject(true) { acc, target ->
            def name = target.name.takeBefore('.wvc')
            try {
                def compileUnit = new WebViewComponentTemplateCompileUnit(
                        AnonymousWebViewComponent,
                        ComponentTemplateSource.of(target),
                        AnonymousWebViewComponent.packageName
                )

                def w = new StringWriter()
                def astVisitor = new AstNodeToScriptVisitor(w)
                compileUnit.groovyCompilationUnit.addPhaseOperation(astVisitor, this.compilePhase)

                def compileResult = compileUnit.compile()

                if (!this.quiet) {
                    println w.toString()
                }

                if (this.writeOut) {
                    def outFile = new File(target.parentFile, name + '.groovy')
                    outFile.write(w.toString())
                }

                if (this.doClasses) {
                    def classesDir = this.classesDir != null
                            ? this.classesDir
                            : new File(target.parentFile, 'classes')
                    classesDir.mkdirs()
                    this.writeClass(classesDir, compileResult.templateClass)
                    compileResult.otherClasses.each { this.writeClass(classesDir, it) }
                }

                return true
            } catch (Exception e) {
                e.printStackTrace()
                return false
            }
        }
        return success ? 0 : 1
    }

}
