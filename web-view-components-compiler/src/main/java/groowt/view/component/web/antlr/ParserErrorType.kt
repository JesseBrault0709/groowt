package groowt.view.component.web.antlr

enum class ParserErrorType(val message: String) {
    NO_VIABLE_ALTERNATIVE("No viable alternative."),
    INPUT_MISMATCH("Input mismatch."),
    FAILED_PREDICATE("Input failed predicate.")
}
