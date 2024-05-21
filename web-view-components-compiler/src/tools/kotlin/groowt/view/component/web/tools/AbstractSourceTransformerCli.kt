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
        names = ["--n", "--dry-run"],
        description = ["Do a dry run; do not output files to disk."]
    )
    protected var dryRun: Boolean = false

    @Option(
        names = ["-y", "--yes"],
        description = ["Automatically write output files if there are no processing errors."]
    )
    protected var autoYes: Boolean = false

    @Option(
        names = ["-W", "--write-over"],
        description = ["If an output file already exists, write over it without asking."]
    )
    protected var autoWriteOver: Boolean = false

    @Option(
        names = ["-v", "--verbose"],
        description = ["Log verbosely to the console."]
    )
    protected var verbose: Boolean = false

    private val scanner = Scanner(System.`in`)

    protected fun getYesNo(prompt: String, allowAuto: Boolean = true): Boolean {
        if (this.autoYes && allowAuto) {
            return true
        } else {
            print("$prompt (y/n) ")
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
        if (this.dryRun) {
            println("Dry-run: would write to $resolvedTarget")
        } else {
            println("Writing to $resolvedTarget...")
            Files.writeString(resolvedTarget, text)
        }
    }

    protected fun writeToDisk(target: Path, text: String) {
        val outputDir = getOutputDir()
        if (outputDir != null) {
            Files.createDirectories(outputDir)
        }
        val resolvedTarget = outputDir?.resolve(target) ?: target
        if (Files.exists(resolvedTarget) && !autoWriteOver) {
            if (getYesNo("$resolvedTarget already exists. Write over?")) {
                doWrite(resolvedTarget, text)
            } else {
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
