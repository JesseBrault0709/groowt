package groowt.view.component.web.antlr

import org.antlr.v4.runtime.*

class ParserErrorListener : BaseErrorListener() {

    private val errors: MutableList<ParserError> = ArrayList()

    fun getErrors(): List<ParserError> = this.errors

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
            is NoViableAltException -> {
                val error = ParserError(ParserErrorType.NO_VIABLE_ALTERNATIVE, e.offendingToken, parser.context)
                errors.add(error)
            }
            is InputMismatchException -> {
                val error = MismatchedInputParserError(
                    ParserErrorType.INPUT_MISMATCH,
                    e.offendingToken,
                    parser.context,
                    e.expectedTokens.toSet()
                )
                errors.add(error)
            }
            is FailedPredicateException -> {
                val error = ParserError(ParserErrorType.FAILED_PREDICATE, e.offendingToken, parser.context)
                errors.add(error)
            }
        }
    }

}
