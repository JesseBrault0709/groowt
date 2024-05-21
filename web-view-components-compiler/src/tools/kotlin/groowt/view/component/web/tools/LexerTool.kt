@file:JvmName("LexerTool")
package groowt.view.component.web.tools

import groowt.view.component.web.antlr.formatToken
import groowt.view.component.web.antlr.runLexerAllTokensRaw

fun main(args: Array<String>) {
    val options = processArgs(args)
    configureLog(options.logLevel)
    val input = getInput(options.source)
    runLexerAllTokensRaw(input).forEachIndexed { i, t -> println("$i: ${formatToken(t)}") }
}
