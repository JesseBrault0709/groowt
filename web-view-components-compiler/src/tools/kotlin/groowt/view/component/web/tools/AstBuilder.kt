package groowt.view.component.web.tools

import groowt.view.component.web.analysis.MismatchedComponentTypeError
import groowt.view.component.web.analysis.checkForMismatchedComponentTypeErrors
import groowt.view.component.web.antlr.*
import groowt.view.component.web.antlr.WebViewComponentsLexerBase.ERROR
import groowt.view.component.web.antlr.WebViewComponentsLexerBase.HIDDEN
import groowt.view.component.web.antlr.WebViewComponentsParser.CompilationUnitContext
import groowt.view.component.web.ast.DefaultAstBuilder
import groowt.view.component.web.ast.DefaultNodeFactory
import groowt.view.component.web.ast.formatAst
import groowt.view.component.web.ast.node.CompilationUnitNode
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ConsoleErrorListener
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.system.exitProcess

@Command(
    name = "astBuilder",
    description = ["Create an AST from a .wvc file."],
    mixinStandardHelpOptions = true,
    version = ["0.1.0"]
)
open class AstBuilder : AbstractSourceTransformerCli() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            exitProcess(CommandLine(AstBuilder()).execute(*args))
        }

    }

    @Option(
        names = ["-s", "--suffix"],
        description = ["The suffix (not extension!) to append to the output file."]
    )
    protected var suffix: String? = null

    @Option(
        names = ["-e", "--extension"],
        description = ["The extension for output files."],
        defaultValue = ".txt"
    )
    protected lateinit var extension: String

    @Option(
        names = ["-d", "--output-dir"],
        description = ["The output directory."],
        defaultValue = ".",
        paramLabel = "outputDir"
    )
    protected lateinit var myOutputDir: Path

    @Option(
        names = ["--strict"],
        description = ["If true, do not recover from syntax errors during parsing."],
        negatable = true
    )
    protected var strict = true

    protected open fun onLexerErrors(errors: List<LexerError>): Boolean {
        System.err.println("There were lexer errors.")
        errors.forEach { System.err.println(formatLexerError(it)) }
        return this.getYesNo("Do you wish to try again?", false)
    }

    protected open fun onParserErrors(errors: List<ParserError>): Boolean {
        System.err.println("There were parser errors.")
        errors.forEach { System.err.println(formatParserError(it)) }
        return this.getYesNo("Do you wish to try again?", false)
    }

    protected open fun onMismatchedErrors(errors: List<MismatchedComponentTypeError>): Boolean {
        System.err.println("There were mismatched component type errors.")
        errors.forEach { System.err.println(it.message) }
        return getYesNo("Do you wish to try again?", false)
    }

    protected open fun onException(phase: String, e: Exception): Boolean {
        System.err.println("There was an exception during $phase: $e")
        if (this.verbose) {
            e.printStackTrace(System.err)
        }
        return this.getYesNo("Do you wish to try again?", false)
    }

    protected open fun getFullTargetPath(target: Path): Path =
        Path.of(target.nameWithoutExtension + (suffix ?: "") + extension)

    protected open fun onSuccess(target: Path, tokenList: TokenList, cuNode: CompilationUnitNode): Boolean {
        val formatted = formatAst(cuNode, tokenList)
        if (interactive) {
            println("Please review the following ast:\n$formatted")
        } else {
            println(formatted)
        }
        if (getYesNo("Do you wish to write to disk?", true)) {
            writeToDisk(getFullTargetPath(target), formatted)
            return false
        } else {
            return getYesNo("Do you wish to redo this file?", false)
        }
    }

    override fun getOutputDir() = myOutputDir

    override fun transform(target: Path): Int {
        if (interactive) {
            println("Building ast for $target")
        }
        while (true) {
            val parseResult: Pair<TokenList, CompilationUnitContext> = try {
                val input = CharStreams.fromPath(target)

                val lexer = WebViewComponentsLexer(input)
                lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
                val lexerErrorListener = LexerErrorListener()
                lexer.addErrorListener(lexerErrorListener)

                val tokenStream = if (strict) {
                    WebViewComponentsTokenStream(lexer) // only ignore hidden (default)
                } else {
                    WebViewComponentsTokenStream(lexer, setOf(HIDDEN, ERROR)) // ignore hidden and error
                }

                val parser = WebViewComponentsParser(tokenStream)
                parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
                val parserErrorListener = ParserErrorListener()
                parser.addErrorListener(parserErrorListener)

                val cuContext = parser.compilationUnit()

                val lexerErrors = lexerErrorListener.getErrors() + parserErrorListener.getLexerErrors()
                val parserErrors = parserErrorListener.getParserErrors()

                if (lexerErrors.isNotEmpty()) {
                    val recover = this.onLexerErrors(lexerErrors)
                    if (!recover) {
                        return 1
                    }
                } else if (parserErrors.isNotEmpty()) {
                    val recover = this.onParserErrors(parserErrors)
                    if (!recover) {
                        return 1
                    }
                }
                Pair(TokenList(tokenStream), cuContext)
            } catch (e: Exception) {
                val recover = this.onException("parsing", e)
                if (!recover) {
                    return 1
                } else {
                    continue
                }
            }

            val mismatchedErrors = checkForMismatchedComponentTypeErrors(parseResult.second)
            if (mismatchedErrors.isNotEmpty()) {
                val tryAgain = this.onMismatchedErrors(mismatchedErrors)
                if (!tryAgain) {
                    return 1
                } else {
                    continue
                }
            }

            val nodeFactory = DefaultNodeFactory(parseResult.first)
            val astBuilder = DefaultAstBuilder(nodeFactory)
            val cuNode: CompilationUnitNode = try {
                astBuilder.buildCompilationUnit(parseResult.second)
            } catch (e: Exception) {
                val recover = this.onException("ast building", e)
                if (!recover) {
                    return 1
                } else {
                    continue
                }
            }

            val redo = this.onSuccess(target, parseResult.first, cuNode)
            if (!redo) {
                return 0
            }
        }
    }

}
