package groowt.view.component.web.antlr

enum class ParserErrorType(val message: String) {
    NO_VIABLE_ALTERNATIVE("Parser has no viable alternative."),
    INPUT_MISMATCH("Parser input mismatch."),
    FAILED_PREDICATE("Parser input failed predicate.")
}
