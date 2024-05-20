@file:JvmName("LexerUtil")
package groowt.view.web.antlr

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Token

fun runLexerAllTokens(input: CharStream): List<Token> = WebViewComponentsLexer(input).allTokens
