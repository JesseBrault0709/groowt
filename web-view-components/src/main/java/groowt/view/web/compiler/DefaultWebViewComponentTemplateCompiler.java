package groowt.view.web.compiler;

import groowt.view.component.compiler.*;
import groowt.view.web.WebViewComponentBugError;
import groowt.view.web.analysis.MismatchedComponentTypeAnalysis;
import groowt.view.web.analysis.MismatchedComponentTypeError;
import groowt.view.web.antlr.*;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.transpile.DefaultGroovyTranspiler;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.tools.GroovyClass;

import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultWebViewComponentTemplateCompiler
        extends CachingComponentTemplateCompiler<WebViewComponentTemplateCompileUnit>
        implements WebViewComponentTemplateCompiler {

    private final ComponentTemplateCompilerConfiguration configuration;

    public DefaultWebViewComponentTemplateCompiler(ComponentTemplateCompilerConfiguration configuration) {
        this.configuration = configuration;
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

    @Override
    protected ComponentTemplateCompileResult doCompile(WebViewComponentTemplateCompileUnit compileUnit)
            throws ComponentTemplateCompileException {

        final Reader sourceReader;
        try {
            sourceReader = compileUnit.getSource().toReader();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final CompilationUnitParseResult parseResult = ParserUtil.parseCompilationUnit(sourceReader);

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
                throw new MultipleWebViewComponentCompileErrorsException(compileUnit, errorExceptions);
            }
        }

        // check for mismatched type errors
        final List<MismatchedComponentTypeError> mismatchedComponentTypeErrors =
                MismatchedComponentTypeAnalysis.check(parseResult.getCompilationUnitContext());

        if (!mismatchedComponentTypeErrors.isEmpty()) {
            if (mismatchedComponentTypeErrors.size() == 1) {
                throw getException(compileUnit, mismatchedComponentTypeErrors.getFirst());
            } else {
                final var errorExceptions = mismatchedComponentTypeErrors.stream()
                        .map(error -> getException(compileUnit, error))
                        .toList();
                throw new MultipleWebViewComponentCompileErrorsException(compileUnit, errorExceptions);
            }
        }

        // build ast
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());

        // transpile to Groovy
        final var transpiler = new DefaultGroovyTranspiler();

        final var ownerComponentName = compileUnit.getForClass() != AnonymousWebViewComponent.class
                ? compileUnit.getForClass().getSimpleName()
                : "AnonymousWebViewComponent" + System.nanoTime();
        final var templateClassSimpleName = ownerComponentName + "Template";

        final SourceUnit sourceUnit = transpiler.transpile(
                this.configuration,
                compileUnit,
                cuNode,
                templateClassSimpleName
        );
        compileUnit.getGroovyCompilationUnit().addSource(sourceUnit);

        // compile groovy
        try {
            compileUnit.getGroovyCompilationUnit().compile(this.configuration.getToCompilePhase().getPhaseNumber());
        } catch (CompilationFailedException compilationFailedException) {
            throw new WebViewComponentTemplateCompileException(
                    compileUnit,
                    "Error while compiling Groovy.",
                    compilationFailedException
            );
        }

        // get the classes
        final var allClasses = compileUnit.getGroovyCompilationUnit().getClasses();
        GroovyClass templateGroovyClass = null;
        final Set<GroovyClass> otherClasses = new HashSet<>();
        final String actualPackageName = sourceUnit.getAST().getPackageName();
        final String templateClassFqn;
        if (actualPackageName.endsWith(".")) {
            templateClassFqn = actualPackageName + templateClassSimpleName;
        } else {
            templateClassFqn = actualPackageName + "." + templateClassSimpleName;
        }
        for (final GroovyClass groovyClass : allClasses) {
            if (groovyClass.getName().equals(templateClassFqn)) {
                if (templateGroovyClass != null) {
                    throw new IllegalStateException("Already found a templateGroovyClass.");
                }
                templateGroovyClass = groovyClass;
            } else {
                otherClasses.add(groovyClass);
            }
        }

        if (templateGroovyClass == null) {
            throw new WebViewComponentBugError(new IllegalStateException("Did not find templateClass"));
        }

        return new SimpleComponentTemplateCompileResult(
                templateGroovyClass,
                otherClasses
        );
    }

}
