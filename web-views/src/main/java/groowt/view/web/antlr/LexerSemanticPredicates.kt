@file:JvmName("LexerSemanticPredicates")
package groowt.view.web.antlr

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

private val logger: Logger = LoggerFactory.getLogger(
    MethodHandles.lookup().lookupClass()
)

private fun escapeParams(params: Array<out Any?>): Array<Any?> {
    val escapedParams: Array<Any?> = Array(params.size) { null }
    params.forEachIndexed { index, param ->
        escapedParams[index] = when (param) {
            is Char -> escapeChar(param)
            is CharArray -> escapeChars(param)
            is String -> escapeChars(param)
            else -> param
        }
    }
    return escapedParams
}

private fun logDebug(msg: String, vararg params: Any?) {
    if (logger.isDebugEnabled) {
        logger.debug(msg, *escapeParams(params))
    }
}

fun isAnyOf(subject: Char, vararg tests: Char): Boolean {
    var result = false
    for (test in tests) {
        if (subject == test) {
            result = true
            break
        }
    }
    logDebug ("subject: {}, tests: {}, result: {}", subject, tests, result)
    return result
}

fun isAnyOf(subject: Int, vararg tests: Char): Boolean = isAnyOf(subject.toChar(), *tests)

/**
 * Contract, starting with current character (read as regex):
 * - `'[^'] -> true`
 * - `''[^'] -> true`
 * - `''' -> false`
 */
fun canFollowJStringOpening(nextTwo: String): Boolean {
    val result = nextTwo[0] != '\'' || nextTwo[1] != '\''
    logDebug ("nextTwo: {}, result: {}", nextTwo, result)
    return result
}

/**
 * Contract, starting with current character (read as regex):
 * - `"[^"] -> true`
 * - `""[^"] -> true`
 * - `""" -> false}`
 */
fun canFollowGStringOpening(nextTwo: String): Boolean {
    val result = nextTwo[0] != '"' || nextTwo[1] != '"'
    logDebug ("nextTwo: {}, result: {}", nextTwo, result)
    return result
}

fun isIdentifierStartChar(c: Char): Boolean = Character.isJavaIdentifierStart(c)

fun isIdentifierStartChar(subject: Int) = isIdentifierStartChar(subject.toChar())

fun isIdentifierChar(c: Char): Boolean = Character.isJavaIdentifierPart(c)

fun isIdentifierChar(subject: Int) = isIdentifierChar(subject.toChar())

fun isGStringIdentifierStartChar(c: Char): Boolean = c != '$' && isIdentifierStartChar(c)

fun isGStringIdentifierStartChar(subject: Int) = isGStringIdentifierChar(subject.toChar())

fun isGStringIdentifierChar(c: Char): Boolean = c != '$' && isIdentifierChar(c)

fun isGStringIdentifierChar(subject: Int) = isGStringIdentifierChar(subject.toChar())
