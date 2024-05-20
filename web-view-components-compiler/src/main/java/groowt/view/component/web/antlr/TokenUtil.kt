@file:JvmName("TokenUtil")
package groowt.view.component.web.antlr

import groowt.view.component.web.antlr.WebViewComponentsLexer.GStringParts
import groowt.view.component.web.antlr.WebViewComponentsLexer.GroovyTokens
import org.antlr.v4.runtime.Token

fun isGroovyTokenType(token: Token) = isGroovyTokenType(token.type)

fun isGroovyTokenType(type: Int): Boolean = type in GroovyTokens

fun isGStringPart (token: Token) = isGStringPart(token.type)

fun isGStringPart(type: Int): Boolean = type in GStringParts

fun getTokenName (token: Token) = getTokenName(token.type)

fun getTokenName(type: Int): String = WebViewComponentsLexer.VOCABULARY.getDisplayName(type)

fun formatToken(token: Token): String = formatToken(token, ::formatTokenText)

@FunctionalInterface
fun interface TokenTextFormatter {
    fun format(text: String): String
}

fun formatToken(token: Token, textFormatter: TokenTextFormatter): String =
    "${getTokenName(token)}[${token.line},${token.charPositionInLine}](${textFormatter.format(token.text)})"

fun shortFormatToken(token: Token): String =
    "${getTokenName(token)}[${token.line},${token.charPositionInLine},${token.text.length}]"

fun formatTokenText(text: String): String = excerptTokenParts(escapeTokenPartsToList(text))

fun formatTokenPosition(token: Token): String {
    return "line ${token.line}, column ${token.charPositionInLine + 1}"
}

fun excerptToken(token: Token) = excerptToken(token, 30, 7, "...")

fun excerptToken(token: Token, startLength: Int = 30, endLength: Int = 7, separator: String = "..."): String {
    return excerptTokenParts(escapeTokenPartsToList(token.text), startLength, endLength, separator)
}

fun excerptTokenParts(
    parts: List<String>,
    startLength: Int = 30,
    endLength: Int = 7,
    separator: String = "..."
): String {
    val length = getTokenTextPartsLength(parts)
    if (length > 40) {
        val start = joinTokenPartsUntil(parts, startLength)
        val end = joinTokenPartsUntilFromEnd(parts, endLength)
        return start + separator + end
    } else {
        return joinTokenParts(parts)
    }
}

private fun joinTokenParts(parts: List<String>): String =
    parts.fold("") { acc, s -> acc + s }

private fun getTokenTextPartsLength(parts: List<String>): Int =
        parts.fold(0) { acc, s -> acc + s.length }

private fun joinTokenPartsUntil(parts: List<String>, limit: Int): String {
    val b = StringBuilder()
    val iter = parts.iterator()
    while (b.length < limit && iter.hasNext()) {
        val next = iter.next()
        val remaining = limit - b.length
        if (next.length <= remaining) {
            b.append(next)
        } else {
            b.append(next.substring(0, remaining))
        }
    }
    return b.toString()
}

private fun joinTokenPartsUntilFromEnd(parts: List<String>, limit: Int): String {
    val reversedTemp: MutableList<String> = ArrayList()
    val iter = parts.reversed().iterator()
    var length = 0
    while (length < limit && iter.hasNext()) {
        val next = iter.next()
        val remaining = limit - length
        if (next.length <= remaining) {
            reversedTemp.addLast(next.reversed())
            length += next.length
        } else {
            val excerpt = next.reversed()
                .substring(0, remaining)
            reversedTemp.addLast(excerpt)
            length += excerpt.length
        }
    }
    return reversedTemp.reversed().fold("") { acc, reversedPart -> acc + reversedPart.reversed() }
}

fun escapeChars(cs: CharArray): Array<String> = Array(cs.size) { escapeChar(cs[it]) }

fun escapeChars(s: String): String = escapeTokenPartsToList(s).joinToString("")

fun escapeChar(c: Char): String = when (c) {
    '\n' -> "\\n"
    '\r' -> "\\r"
    '\t' -> "\\t"
    else -> c.toString()
}

private fun escapeTokenPartsToList(text: String): List<String> = text.map(::escapeChar)
