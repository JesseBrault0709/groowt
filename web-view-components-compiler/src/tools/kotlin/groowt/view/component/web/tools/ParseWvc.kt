package groowt.view.component.web.tools

import groowt.view.component.web.antlr.*
import groowt.view.component.web.antlr.WebViewComponentsLexerBase.ERROR
import groowt.view.component.web.antlr.WebViewComponentsLexerBase.HIDDEN
import groowt.view.component.web.antlr.WebViewComponentsParser.CompilationUnitContext
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ConsoleErrorListener
import picocli.CommandLine
import picocli.CommandLine.Option
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.system.exitProcess

open class ParseWvc : AbstractSourceTransformerCli() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            exitProcess(CommandLine(ParseWvc()).execute(*args))
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

    protected open fun getOutputPath(target: Path): Path =
        Path.of(target.nameWithoutExtension + (suffix ?: "") + extension)

    protected open fun onSuccess(
        target: Path,
        parser: WebViewComponentsParser,
        cuContext: CompilationUnitContext
    ): Boolean {
        val formatted = formatTree(
            parser = parser,
            tree = cuContext,
            colors = false
        )
        if (this.interactive) {
            println("Please review the following parse tree:\n$formatted")
        } else {
            println(formatted)
        }
        if (this.getYesNo("Write to disk?", true)) {
            this.writeToDisk(getOutputPath(target), formatted)
            return false
        } else {
            return this.getYesNo("Do you wish to redo this file?", false)
        }
    }

    protected open fun onException(e: Exception): Boolean {
        System.err.println("There was an exception during parsing: $e")
        if (this.verbose) {
            e.printStackTrace(System.err)
        }
        return this.getYesNo("Do you wish to try again?", false)
    }

    override fun getOutputDir() = myOutputDir

    override fun transform(target: Path): Int {
        if (interactive) {
            println("Parsing $target")
        }
        var code = 0
        while (true) {
            try {
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
                        code = 1
                        break
                    }
                } else if (parserErrors.isNotEmpty()) {
                    val recover = this.onParserErrors(parserErrors)
                    if (!recover) {
                        code = 1
                        break
                    }
                } else {
                    val redo = this.onSuccess(target, parser, cuContext)
                    if (!redo) {
                        break
                    }
                }
            } catch (e: Exception) {
                val recover = this.onException(e)
                if (!recover) {
                    code = 1
                    break
                }
            }
        }
        return code
    }

}
