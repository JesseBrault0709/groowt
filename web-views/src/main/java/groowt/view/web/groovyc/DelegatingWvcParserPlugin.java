package groowt.view.web.groovyc;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.DefaultComponentTemplateCompilerConfiguration;
import groowt.view.component.compiler.source.ComponentTemplateSource;
import groowt.view.web.analysis.MismatchedComponentTypeAnalysis;
import groowt.view.web.analysis.MismatchedComponentTypeError;
import groowt.view.web.antlr.*;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.compiler.AnonymousWebViewComponent;
import groowt.view.web.compiler.MultipleWebViewComponentCompileErrorsException;
import groowt.view.web.compiler.WebViewComponentTemplateCompileException;
import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.web.transpile.DefaultGroovyTranspiler;
import groowt.view.web.util.SourcePosition;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.apache.groovy.parser.antlr4.Antlr4ParserPlugin;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.ParserPlugin;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.ParserException;
import org.codehaus.groovy.syntax.Reduction;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class DelegatingWvcParserPlugin implements ParserPlugin {

    private final Antlr4ParserPlugin groovyParserPlugin;

    public DelegatingWvcParserPlugin(Antlr4ParserPlugin groovyParserPlugin) {
        this.groovyParserPlugin = groovyParserPlugin;
    }

    @Override
    public Reduction parseCST(SourceUnit sourceUnit, Reader reader) throws CompilationFailedException {
        return this.groovyParserPlugin.parseCST(sourceUnit, reader); // returns null
    }

    protected WebViewComponentTemplateCompileException getException(
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

    protected WebViewComponentTemplateCompileException getException(
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

    protected WebViewComponentTemplateCompileException getException(
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

    protected WebViewComponentTemplateCompileException getException(
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

    protected ParserException translateException(ComponentTemplateCompileException e) {
        final var actual = (WebViewComponentTemplateCompileException) e;
        final SourcePosition sourcePosition = actual.getSourcePosition();
        if (sourcePosition != null) {
            return new ParserException(e.getMessage(), e, sourcePosition.line(), sourcePosition.column());
        } else {
            return new ParserException(e.getMessage(), e, -1, -1);
        }
    }

    @Override
    public ModuleNode buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction cst) throws ParserException {
        final String sourceUnitName = sourceUnit.getName();
        if (sourceUnitName.endsWith(".wvc")) {
            final var compileUnit = new WebViewComponentTemplateCompileUnit(
                    AnonymousWebViewComponent.class,
                    ComponentTemplateSource.of(sourceUnit.getSource().getURI()),
                    "groowt.view.web.groovyc"
            );

            final CompilationUnitParseResult parseResult;
            try {
                parseResult = ParserUtil.parseCompilationUnit(sourceUnit.getSource().getReader());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // check for parser/lexer errors
            final var parseErrors = AntlrUtil.findErrorNodes(parseResult.getCompilationUnitContext());
            if (!parseErrors.isEmpty()) {
                if (parseErrors.getErrorCount() == 1) {
                    final var errorNode = parseErrors.getAll().getFirst();
                    throw this.translateException(getException(compileUnit, errorNode));
                } else {
                    final var errorExceptions = parseErrors.getAll().stream()
                            .map(errorNode -> getException(compileUnit, errorNode))
                            .toList();
                    throw this.translateException(new MultipleWebViewComponentCompileErrorsException(
                            compileUnit,
                            errorExceptions
                    ));
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
                    throw this.translateException(new MultipleWebViewComponentCompileErrorsException(
                            compileUnit,
                            errorExceptions
                    ));
                }
            }

            // build ast
            final var tokenList = new TokenList(parseResult.getTokenStream());
            final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
            final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());

            final var groovyTranspiler = new DefaultGroovyTranspiler();
            try {
                final SourceUnit transpiledSourceUnit = groovyTranspiler.transpile(
                        new DefaultComponentTemplateCompilerConfiguration(),
                        compileUnit,
                        cuNode,
                        sourceUnitName.substring(0, sourceUnitName.length() - 4)
                );
                return transpiledSourceUnit.getAST();
            } catch (ComponentTemplateCompileException e) {
                throw this.translateException(e);
            }
        } else {
            return this.groovyParserPlugin.buildAST(sourceUnit, classLoader, cst);
        }
    }

}
