package groowt.view.component.web.groovyc;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.DefaultComponentTemplateCompilerConfiguration;
import groowt.view.component.compiler.source.ComponentTemplateSource;
import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.analysis.MismatchedComponentTypeAnalysis;
import groowt.view.component.web.analysis.MismatchedComponentTypeError;
import groowt.view.component.web.antlr.*;
import groowt.view.component.web.ast.DefaultAstBuilder;
import groowt.view.component.web.ast.DefaultNodeFactory;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import groowt.view.component.web.compiler.*;
import groowt.view.component.web.transpile.DefaultGroovyTranspiler;
import groowt.view.component.web.util.SourcePosition;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class DelegatingWvcParserPlugin implements ParserPlugin {

    private static final Logger logger = LoggerFactory.getLogger(DelegatingWvcParserPlugin.class);

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
        if (e instanceof WebViewComponentTemplateCompileException single) {
            final SourcePosition sourcePosition = single.getSourcePosition();
            if (sourcePosition != null) {
                return new ParserException(e.getMessage(), e, sourcePosition.line(), sourcePosition.column());
            } else {
                return new ParserException(e.getMessage(), e, 1, 1);
            }
        } else if (e instanceof MultipleWebViewComponentCompileErrorsException multiple) {
            return new ParserException("There were multiple errors during compilation/transpilation.", multiple, 1, 1);
        } else {
            throw new WebViewComponentBugError(
                    "Cannot determine the type of non-WebViewComponent compile exception: "
                            + e.getClass().getName()
            );
        }
    }

    protected void logException(WebViewComponentTemplateCompileException e) {
        logger.error(e.getMessage());
    }

    protected void logException(MultipleWebViewComponentCompileErrorsException e) {
        logger.error(e.getMessage());
    }

    @Override
    public ModuleNode buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction cst) throws ParserException {
        final String sourceUnitFullName = sourceUnit.getName();
        final int lastSlashIndex = sourceUnitFullName.lastIndexOf(File.separator);
        final String sourceUnitFileName = sourceUnitFullName.substring(lastSlashIndex + 1);
        if (sourceUnitFileName.endsWith(".wvc")) {
            final var compileUnit = new DefaultWebViewComponentTemplateCompileUnit(
                    AnonymousWebViewComponent.class,
                    ComponentTemplateSource.of(sourceUnit.getSource().getURI()),
                    "" // default package
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
                    final var multiple = new MultipleWebViewComponentCompileErrorsException(
                            compileUnit,
                            errorExceptions
                    );
                    this.logException(multiple);
                    throw this.translateException(multiple);
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
                    final var multiple = new MultipleWebViewComponentCompileErrorsException(
                            compileUnit,
                            errorExceptions
                    );
                    this.logException(multiple);
                    throw this.translateException(multiple);
                }
            }

            // build ast
            final var tokenList = new TokenList(parseResult.getTokenStream());
            final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
            final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());

            final var groovyTranspiler = new DefaultGroovyTranspiler();
            final String nameWithoutExtension = sourceUnitFileName.substring(0, sourceUnitFileName.length() - 4);
            try {
                final SourceUnit transpiledSourceUnit = groovyTranspiler.transpile(
                        new DefaultComponentTemplateCompilerConfiguration(),
                        compileUnit,
                        cuNode,
                        nameWithoutExtension
                );
                return transpiledSourceUnit.getAST();
            } catch (ComponentTemplateCompileException e) {
                if (e instanceof WebViewComponentTemplateCompileException single) {
                    this.logException(single);
                } else if (e instanceof MultipleWebViewComponentCompileErrorsException multiple) {
                    this.logException(multiple);
                } else {
                    throw new WebViewComponentBugError(
                            "Could not determine type of non-WebViewComponent compile exception: "
                                    + e.getClass().getName()
                    );
                }
                throw this.translateException(e);
            }
        } else {
            return this.groovyParserPlugin.buildAST(sourceUnit, classLoader, cst);
        }
    }

}
