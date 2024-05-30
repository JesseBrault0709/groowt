package groowt.view.component.web.compiler;

import groowt.view.component.web.analysis.MismatchedComponentTypeAnalysis;
import groowt.view.component.web.analysis.MismatchedComponentTypeError;
import groowt.view.component.web.antlr.*;
import groowt.view.component.web.ast.DefaultAstBuilder;
import groowt.view.component.web.ast.DefaultNodeFactory;
import groowt.view.component.web.ast.node.CompilationUnitNode;

import java.io.IOException;
import java.util.List;

public final class CompilerPipeline {

    private static WebViewComponentTemplateCompileException getException(
            WebViewComponentTemplateCompileUnit compileUnit,
            LexerError lexerError
    ) {
        final String formatted = LexerErrorKt.formatLexerError(lexerError);
        return new WebViewComponentTemplateCompileException(
                compileUnit, formatted
        );
    }

    private static WebViewComponentTemplateCompileException getException(
            WebViewComponentTemplateCompileUnit compileUnit,
            ParserError parserError
    ) {
        final String formatted = ParserErrorKt.formatParserError(parserError);
        return new WebViewComponentTemplateCompileException(
                compileUnit, formatted
        );
    }

    private static WebViewComponentTemplateCompileException getException(
            WebViewComponentTemplateCompileUnit compileUnit,
            MismatchedComponentTypeError error
    ) {
        final var exception = new WebViewComponentTemplateCompileException(
                compileUnit,
                error.getMessage()
        );
        exception.setParserRuleContext(error.getComponent());
        return exception;
    }

    public static CompilationUnitNode parseAndBuildAst(WebViewComponentTemplateCompileUnit compileUnit)
            throws WebViewComponentTemplateCompileException, MultipleWebViewComponentCompileErrorsException {
        final CompilationUnitParseResult parseResult;
        try {
            parseResult = ParserUtil.parseCompilationUnit(compileUnit.getGroovyReaderSource().getReader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // check for lexer/parser errors
        final var lexerErrors = parseResult.getLexerErrors();
        final var parserErrors = parseResult.getParserErrors();

        if (!lexerErrors.isEmpty()) {
            if (lexerErrors.size() == 1) {
                throw getException(compileUnit, lexerErrors.getFirst());
            } else {
                final var exceptions = lexerErrors.stream()
                        .map(error -> getException(compileUnit, error))
                        .toList();
                throw new MultipleWebViewComponentCompileErrorsException(
                        compileUnit,
                        exceptions
                );
            }
        }

        if (!parserErrors.isEmpty()) {
            if (parserErrors.size() == 1) {
                throw getException(compileUnit, parserErrors.getFirst());
            } else {
                final var exceptions = parserErrors.stream()
                        .map(error -> getException(compileUnit, error))
                        .toList();
                throw new MultipleWebViewComponentCompileErrorsException(
                        compileUnit,
                        exceptions
                );
            }
        }

        // check for mismatched type errors
        final List<MismatchedComponentTypeError> mismatchedComponentTypeErrors =
                MismatchedComponentTypeAnalysis.checkForMismatchedComponentTypeErrors(parseResult.getCompilationUnitContext());

        if (!mismatchedComponentTypeErrors.isEmpty()) {
            if (mismatchedComponentTypeErrors.size() == 1) {
                throw new RuntimeException(getException(compileUnit, mismatchedComponentTypeErrors.getFirst()));
            } else {
                final var errorExceptions = mismatchedComponentTypeErrors.stream()
                        .map(error -> getException(compileUnit, error))
                        .toList();
                throw new MultipleWebViewComponentCompileErrorsException(
                        compileUnit,
                        errorExceptions
                );
            }
        }

        // build ast
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        return astBuilder.buildCompilationUnit(parseResult.getCompilationUnitContext());
    }

    private CompilerPipeline() {}

}
