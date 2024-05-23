package groowt.view.component.web.tools

import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.Callable

abstract class AbstractSourceTransformerCli : Callable<Int> {

    @Parameters(
        arity = "1..*",
        description = ["The wvc source file(s) to process."]
    )
    protected lateinit var targets: List<Path>

    @Option(
        names = ["-i", "--interactive"],
        description = ["Allow interactive recovery from errors. If false, implies -W."]
    )
    protected var interactive = false

    @Option(
        names = ["-P", "--print-only"],
        description = ["Only print result(s) to stdout; do not write files to disk."]
    )
    protected var printOnly = false

    @Option(
        names = ["-W", "--write-over"],
        description = ["If an output file already exists, write over it without asking."]
    )
    protected var autoWriteOver = false

    @Option(
        names = ["-v", "--verbose"],
        description = ["Log exceptions and errors verbosely to stderr."]
    )
    protected var verbose = false

    private val scanner = Scanner(System.`in`)

    protected open fun getYesNo(prompt: String, fallback: Boolean): Boolean {
        if (!interactive) {
            return fallback
        } else {
            print("$prompt (y/n): ")
            while (true) {
                if (this.scanner.hasNextLine()) {
                    val input = this.scanner.nextLine()
                    if (input.contains("n")) {
                        return false
                    } else if (input.contains("y")) {
                        return true
                    }
                }
            }
        }
    }

    private fun doWrite(resolvedTarget: Path, text: String) {
        if (!this.printOnly) {
            Files.writeString(resolvedTarget, text)
        }
    }

    protected open fun writeToDisk(target: Path, text: String) {
        val outputDir = getOutputDir()
        if (outputDir != null) {
            Files.createDirectories(outputDir)
        }
        val resolvedTarget = outputDir?.resolve(target) ?: target
        if (Files.exists(resolvedTarget) && !autoWriteOver) {
            if (getYesNo("$resolvedTarget already exists. Write over?", true)) {
                doWrite(resolvedTarget, text)
            } else if (interactive) {
                println("Skipping writing to $resolvedTarget")
            }
        } else {
            doWrite(resolvedTarget, text)
        }
    }

    protected abstract fun transform(target: Path): Int

    protected abstract fun getOutputDir(): Path?

    override fun call(): Int {
        return this.targets.fold(0) { acc, target ->
            acc.or(this.transform(target))
        }
    }

}
