package groowt.view.web.tools

import groovy.console.ui.AstNodeToScriptVisitor
import groowt.util.di.DefaultRegistryObjectFactory
import groowt.view.web.antlr.ParserUtil
import groowt.view.web.antlr.TokenList
import groowt.view.web.ast.DefaultAstBuilder
import groowt.view.web.ast.DefaultNodeFactory
import groowt.view.web.ast.node.CompilationUnitNode
import groowt.view.web.transpile.DefaultGroovyTranspiler
import groowt.view.web.transpile.DefaultTranspilerConfiguration
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.io.FileReaderSource
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

    @Override
    Integer call() throws Exception {
        boolean success = this.targets.inject(true) { acc, target ->
            def name = target.name.takeBefore('.wvc')
            try {
                def parseResult = ParserUtil.parseCompilationUnit(target)
                def tokenList = new TokenList(parseResult.tokenStream)
                def astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList))
                def cuNode = astBuilder.build(parseResult.compilationUnitContext) as CompilationUnitNode
                def config = new CompilerConfiguration().tap {
                    it.targetDirectory = this.doClasses
                            ? this.classesDir ?: new File(target.parentFile, 'classes')
                            : File.createTempDir()
                }
                def gcu = new CompilationUnit(config)

                def w = new StringWriter()
                def astVisitor = new AstNodeToScriptVisitor(w)
                gcu.addPhaseOperation(astVisitor, this.compilePhase)

                def transpiler = new DefaultGroovyTranspiler(
                        gcu,
                        this.defaultPackageName,
                        { new DefaultTranspilerConfiguration() }
                )
                transpiler.transpile(
                        cuNode,
                        tokenList,
                        name.capitalize(),
                        new FileReaderSource(target, new CompilerConfiguration())
                )
                gcu.compile(this.compilePhase)

                if (this.writeOut) {
                    def outFile = new File(target.parentFile, name + '.groovy')
                    outFile.write(w.toString())
                }
                if (!this.quiet) {
                    println w.toString()
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
