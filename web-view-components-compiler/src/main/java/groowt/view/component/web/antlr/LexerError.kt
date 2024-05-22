package groowt.view.component.web.antlr

import groowt.view.component.web.util.SourcePosition

data class LexerError(
    val type: LexerErrorType,
    val sourcePosition: SourcePosition,
    val badText: String,
    val lexerMode: Int
)

fun formatLexerError(lexerError: LexerError): String {
    return "At ${lexerError.sourcePosition.toStringLong()}: ${lexerError.type.message} " +
            "Bad input: '${escapeChars(lexerError.badText)}'. " +
            "(mode: ${WebViewComponentsLexer.modeNames[lexerError.lexerMode]})."
}
