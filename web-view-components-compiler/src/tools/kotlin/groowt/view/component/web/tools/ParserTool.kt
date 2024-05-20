@file:JvmName("ParserTool")
package groowt.view.component.web.tools

import groowt.view.component.web.antlr.WebViewComponentsLexer
import groowt.view.component.web.antlr.WebViewComponentsTokenStream
import groowt.view.component.web.antlr.formatTree
import groowt.view.component.web.antlr.parseCompilationUnit

fun main(args: Array<String>) {
    val options = processArgs(args)
    configureLog(options.logLevel)
    val input = getInput(options.source)
    val lexer = WebViewComponentsLexer(input)
    val tokenStream = WebViewComponentsTokenStream(lexer)
    parseCompilationUnit(tokenStream) { cu, parser ->
        println(formatTree(parser, cu))
    }
}
