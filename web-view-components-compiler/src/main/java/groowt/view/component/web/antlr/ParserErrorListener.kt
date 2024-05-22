package groowt.view.component.web.antlr

import groowt.view.component.web.util.SourcePosition
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.Interval

class ParserErrorListener : BaseErrorListener() {

    private val lexerErrors: MutableList<LexerError> = ArrayList()
    private val parserErrors: MutableList<ParserError> = ArrayList()

    fun getLexerErrors(): List<LexerError> = this.lexerErrors
    fun getParserErrors(): List<ParserError> = this.parserErrors

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException
    ) {
        val parser = recognizer as WebViewComponentsParser
        when (e) {
            is LexerNoViableAltException -> {
                val lexer = e.recognizer as WebViewComponentsLexer
                val sourcePosition = SourcePosition(line, charPositionInLine + 1)
                val badText = lexer.inputStream.getText(Interval.of(e.startIndex, e.startIndex))
                val error = LexerError(LexerErrorType.NO_VIABLE_ALTERNATIVE, sourcePosition, badText, lexer._mode)
                this.lexerErrors.add(error)
            }
            is NoViableAltException -> {
                val error = ParserError(ParserErrorType.NO_VIABLE_ALTERNATIVE, e.offendingToken, parser.context)
                parserErrors.add(error)
            }
            is InputMismatchException -> {
                val error = MismatchedInputParserError(
                    ParserErrorType.INPUT_MISMATCH,
                    e.offendingToken,
                    parser.context,
                    e.expectedTokens.toSet()
                )
                parserErrors.add(error)
            }
            is FailedPredicateException -> {
                val error = ParserError(ParserErrorType.FAILED_PREDICATE, e.offendingToken, parser.context)
                parserErrors.add(error)
            }
        }
    }

}
