@file:JvmName("LexerUtil")
package groowt.view.component.web.antlr

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Token

fun runLexerAllTokensRaw(input: CharStream): List<Token> = WebViewComponentsLexer(input).allTokens

fun runLexerAllTokens(input: CharStream, withEOF: Boolean = false): List<Token> {
    val lexer = WebViewComponentsLexer(input)
    val stream = WebViewComponentsTokenStream(lexer)
    return if (withEOF) stream.getAllTokens() else stream.getAllTokens()
}

fun runLexerAllTokens(input: CharStream) = runLexerAllTokens(input, withEOF = false)
