@file:JvmName("ToolUtil")
package groowt.view.web.tools

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import java.lang.invoke.MethodHandles
import java.net.URI

private fun getLogConfiguration(): URI =
    MethodHandles.lookup().lookupClass().getResource("/log4j2.xml")?.toURI()
        ?: throw RuntimeException("cannot find resource /log4j2.xml")

internal fun configureLog(levelName: String) {
    val level = Level.toLevel(levelName)
    if (level == null) {
        LogManager.getRootLogger().warn("Unknown log level: $levelName")
    } else {
        Configurator.reconfigure(getLogConfiguration())
        Configurator.setLevel(MethodHandles.lookup().lookupClass().packageName, level)
    }
}

internal sealed interface Source

internal class FileSource(private val fileName: String) : Source {
    fun get() = this.fileName
}

internal class StringSource(private val s: String) : Source {
    fun get() = this.s
}

internal data class CliOptions(val source: Source, val logLevel: String)

internal fun processArgs(args: Array<String>): CliOptions {
    var source: Source? = null
    var logLevel: String? = null
    val argsIter = args.iterator()
    while (argsIter.hasNext()) {
        when (val current = argsIter.next()) {
            "--file" -> {
                val fileName = if (argsIter.hasNext()) {
                    argsIter.next()
                } else {
                    throw IllegalArgumentException("--file must be followed by a file name!")
                }
                source = FileSource(fileName)
            }
            "--source" -> {
                val rawSource = if (argsIter.hasNext()) {
                    argsIter.next()
                } else {
                    throw IllegalArgumentException("--source must be followed by a string source!")
                }
                source = StringSource(rawSource)
            }
            else -> {
                if (current.startsWith("--logLevel")) {
                    logLevel = current.substringAfter("=")
                } else {
                    throw IllegalArgumentException("Unknown option: $current")
                }
            }
        }
    }

    if (source == null) {
        throw IllegalArgumentException("must provide a source with either --file or --source")
    }

    return CliOptions(source, logLevel ?: "info")
}

internal fun getInput(source: Source): CharStream = when (source) {
    is FileSource -> CharStreams.fromFileName(source.get())
    is StringSource -> CharStreams.fromString(source.get())
}
