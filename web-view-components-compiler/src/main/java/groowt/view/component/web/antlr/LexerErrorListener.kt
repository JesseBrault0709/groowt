package groowt.view.component.web.antlr

import groowt.view.component.web.util.SourcePosition
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.misc.Interval
import java.util.*

class LexerErrorListener : ANTLRErrorListener {

    private val errors: MutableList<LexerError> = ArrayList()

    fun getErrors(): List<LexerError> = this.errors

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException
    ) {
        if (e is LexerNoViableAltException) {
            val sourcePosition = SourcePosition(line, charPositionInLine + 1)
            val lexerError = LexerError(
                LexerErrorType.NO_VIABLE_ALTERNATIVE,
                sourcePosition,
                e.inputStream.getText(Interval.of(e.startIndex, e.startIndex)),
                (recognizer as WebViewComponentsLexer)._mode
            )
            errors.add(lexerError)
        } else {
            throw e
        }
    }

    override fun reportAmbiguity(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        exact: Boolean,
        ambigAlts: BitSet?,
        configs: ATNConfigSet?
    ) {
        throw UnsupportedOperationException()
    }

    override fun reportAttemptingFullContext(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        conflictingAlts: BitSet?,
        configs: ATNConfigSet?
    ) {
        throw UnsupportedOperationException()
    }

    override fun reportContextSensitivity(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        prediction: Int,
        configs: ATNConfigSet?
    ) {
        throw UnsupportedOperationException()
    }

}
