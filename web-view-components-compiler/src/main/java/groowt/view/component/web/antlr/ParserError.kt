package groowt.view.component.web.antlr

import groowt.view.component.web.util.SourcePosition
import groowt.view.component.web.util.excerpt
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token

open class ParserError {

    val type: ParserErrorType
    val offendingToken: Token?
    val offendingSymbol: Any?
    val msg: String?
    val sourcePosition: SourcePosition
    val context: ParserRuleContext

    constructor(
        type: ParserErrorType,
        offendingToken: Token?,
        sourcePosition: SourcePosition,
        context: ParserRuleContext
    ) {
        this.type = type
        this.offendingToken = offendingToken
        this.offendingSymbol = offendingToken
        this.msg = null;
        this.sourcePosition = sourcePosition
        this.context = context
    }

    constructor(
        type: ParserErrorType,
        offendingSymbol: Any?,
        msg: String,
        sourcePosition: SourcePosition,
        context: ParserRuleContext
    ) {
        this.type = type
        this.offendingToken = null
        this.offendingSymbol = offendingSymbol
        this.msg = msg
        this.sourcePosition = sourcePosition
        this.context = context
    }

}

class MismatchedInputParserError(
    type: ParserErrorType,
    offendingToken: Token,
    sourcePosition: SourcePosition,
    context: ParserRuleContext,
    val expectedTokenTypes: Set<Int>
) : ParserError(type, offendingToken, sourcePosition, context)

fun formatParserError(error: ParserError): String {
    val sb = StringBuilder()
    sb.append("At ")
        .append(error.sourcePosition.toStringLong())
        .append(": ")
    if (error.type != ParserErrorType.UNKNOWN) {
        sb.append(error.type.message)
    } else if (error.msg != null) {
        sb.append(error.msg + ".")
    } else {
        sb.append("Parser unknown error.")
    }
    if (error.offendingToken != null) {
        sb.append(" Offending token: ")
            .append(formatTokenForError(error.offendingToken))
            .append(". ")
    } else if (error.offendingSymbol != null) {
        sb.append(" Offending symbol: ${error.offendingSymbol}. ")
    }
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
