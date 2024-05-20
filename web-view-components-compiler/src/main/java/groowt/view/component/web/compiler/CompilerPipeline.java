package groowt.view.component.web.compiler;

import groowt.view.component.web.analysis.MismatchedComponentTypeAnalysis;
import groowt.view.component.web.analysis.MismatchedComponentTypeError;
import groowt.view.component.web.antlr.*;
import groowt.view.component.web.ast.DefaultAstBuilder;
import groowt.view.component.web.ast.DefaultNodeFactory;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;

import java.io.IOException;
import java.util.List;

public final class CompilerPipeline {

    private static WebViewComponentTemplateCompileException getException(
            WebViewComponentTemplateCompileUnit compileUnit,
            TerminalNode terminalNode
    ) {
        final Token offending = terminalNode.getSymbol();
        final var exception = new WebViewComponentTemplateCompileException(
                compileUnit, "Invalid token '" + TokenUtil.excerptToken(offending) + "'."
        );
        exception.setTerminalNode(terminalNode);
        return exception;
    }

    private static WebViewComponentTemplateCompileException getException(
            WebViewComponentTemplateCompileUnit compileUnit,
            ParserRuleContext parserRuleContext
    ) {
        final var exception = new WebViewComponentTemplateCompileException(
                compileUnit,
                "Parser error: " + parserRuleContext.exception.getMessage(),
                parserRuleContext.exception
        );
        exception.setParserRuleContext(parserRuleContext);
        return exception;
    }

    private static WebViewComponentTemplateCompileException getException(
            WebViewComponentTemplateCompileUnit compileUnit,
            Tree tree
    ) {
        if (tree instanceof ParserRuleContext parserRuleContext) {
            return getException(compileUnit, parserRuleContext);
        } else if (tree instanceof TerminalNode terminalNode) {
            return getException(compileUnit, terminalNode);
        } else {
            return new WebViewComponentTemplateCompileException(
                    compileUnit,
                    "Error at parser/lexer node " + tree.toString()
            );
        }
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

        // check for parser/lexer errors
        final var parseErrors = AntlrUtil.findErrorNodes(parseResult.getCompilationUnitContext());
        if (!parseErrors.isEmpty()) {
            if (parseErrors.getErrorCount() == 1) {
                final var errorNode = parseErrors.getAll().getFirst();
                throw getException(compileUnit, errorNode);
            } else {
                final var errorExceptions = parseErrors.getAll().stream()
                        .map(errorNode -> getException(compileUnit, errorNode))
                        .toList();
                throw new MultipleWebViewComponentCompileErrorsException(
                        compileUnit,
                        errorExceptions
                );
            }
        }

        // check for mismatched type errors
        final List<MismatchedComponentTypeError> mismatchedComponentTypeErrors =
                MismatchedComponentTypeAnalysis.check(parseResult.getCompilationUnitContext());

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
        return (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());
    }

    private CompilerPipeline() {}

}
