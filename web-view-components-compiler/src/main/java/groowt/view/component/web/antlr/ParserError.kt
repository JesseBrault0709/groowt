package groowt.view.component.web.antlr

import groowt.view.component.web.util.SourcePosition
import groowt.view.component.web.util.excerpt
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token

open class ParserError(val type: ParserErrorType, val offending: Token, val context: ParserRuleContext)

class MismatchedInputParserError(
    type: ParserErrorType,
    offending: Token,
    context: ParserRuleContext,
    val expectedTokenTypes: Set<Int>
) : ParserError(type, offending, context)

fun format(error: ParserError): String {
    val sb = StringBuilder()
    val sourcePosition = SourcePosition.fromStartOfToken(error.offending)
    sb.append("At ")
        .append(sourcePosition.toStringLong())
        .append(": ")
        .append(error.type.message)
        .append(" Offending token: ")
        .append(formatTokenForError(error.offending))
        .append(". ")
    if (error is MismatchedInputParserError) {
        sb.append("Expected any of: ")
            .append(formatExpected(error.expectedTokenTypes))
            .append(". ")
    }
    sb.append("(" + formatContext(error.context) + ").")
    return sb.toString()
}

private fun formatTokenForError(token: Token): String {
    return "'${token.text}' (${getTokenName(token)})"
}

private fun formatContext(context: ParserRuleContext): String {
    val sb = StringBuilder()
    sb.append("context: ${context.javaClass.simpleName}")
    if (context.text.isNotEmpty()) {
        sb.append(", text: ")
            .append(escapeChars(excerpt(context.text)))
    }
    return sb.toString()
}

private fun formatExpected(expectedTokenTypes: Set<Int>): String = expectedTokenTypes.joinToString { getTokenName(it) }
