package groowt.view.component.web.tools

import groowt.view.component.web.antlr.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.Token
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.system.exitProcess

@Command(
    name = "tokenizeWvc",
    description = ["Tokenizes a Web View Component source file."],
    mixinStandardHelpOptions = true,
    version = ["0.1.0"]
)
open class TokenizeWvc : AbstractSourceTransformerCli() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            exitProcess(CommandLine(TokenizeWvc()).execute(*args))
        }

    }

    @Option(
        names = ["-s", "--suffix"],
        description = ["The suffix (not extension!) to append to output file names."]
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

    protected fun onErrors(errors: List<LexerError>): Boolean {
        System.err.println("There were errors during tokenization.")
        errors.forEach { System.err.println(formatLexerError(it)) }
        return this.getYesNo("Do you wish to try again?", false)
    }

    protected fun getOutputPath(target: Path): Path =
        Path.of(target.nameWithoutExtension + (suffix ?: "") + extension)

    protected fun onSuccess(target: Path, allTokens: List<Token>): Boolean {
        val formatted = allTokens.mapIndexed { index, token ->
            "$index: ${formatToken(token)}"
        }.joinToString(separator = "\n")
        if (interactive) {
            println("Please review the following tokens:\n$formatted")
        }
        if (this.getYesNo("Write to disk?", true)) {
            this.writeToDisk(getOutputPath(target), formatted)
            return false
        } else {
            return this.getYesNo("Do you wish to redo this file?", false)
        }
    }

    protected fun onException(e: Exception): Boolean {
        System.err.println("There was an exception during processing: $e")
        if (this.verbose) {
            e.printStackTrace(System.err)
        }
        return this.getYesNo("Do you wish to try again?", false)
    }

    override fun getOutputDir() = myOutputDir

    override fun transform(target: Path): Int {
        if (interactive) {
            println("Tokenizing $target")
        }
        var code = 0
        while (true) {
            try {
                val input = CharStreams.fromPath(target)

                val lexer = WebViewComponentsLexer(input)
                lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
                val errorListener = LexerErrorListener()
                lexer.addErrorListener(errorListener)

                val tokenStream = WebViewComponentsTokenStream(lexer, setOf()) // include everything (ignore nothing)
                val allTokens = tokenStream.getAllTokensSkipEOF()

                val errors = errorListener.getErrors()
                if (errors.isNotEmpty()) {
                    val recover = this.onErrors(errors)
                    if (!recover) {
                        code = 1
                        break
                    }
                } else {
                    val redo = this.onSuccess(target, allTokens)
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
