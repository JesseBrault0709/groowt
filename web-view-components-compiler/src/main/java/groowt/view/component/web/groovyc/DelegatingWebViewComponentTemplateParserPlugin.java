package groowt.view.component.web.groovyc;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.DefaultComponentTemplateCompilerConfiguration;
import groowt.view.component.compiler.source.ComponentTemplateSource;
import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import groowt.view.component.web.compiler.*;
import groowt.view.component.web.transpile.DefaultGroovyTranspiler;
import groowt.view.component.web.util.SourcePosition;
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
import java.io.Reader;

public class DelegatingWebViewComponentTemplateParserPlugin implements ParserPlugin {

    private static final Logger logger = LoggerFactory.getLogger(DelegatingWebViewComponentTemplateParserPlugin.class);

    private final Antlr4ParserPlugin groovyParserPlugin;

    public DelegatingWebViewComponentTemplateParserPlugin(Antlr4ParserPlugin groovyParserPlugin) {
        this.groovyParserPlugin = groovyParserPlugin;
    }

    @Override
    public Reduction parseCST(SourceUnit sourceUnit, Reader reader) throws CompilationFailedException {
        return this.groovyParserPlugin.parseCST(sourceUnit, reader); // returns null
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

    protected void logException(ComponentTemplateCompileException e) {
        logger.error(e.getMessage());
    }

    @Override
    public ModuleNode buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction cst) throws ParserException {
        final String sourceUnitFullName = sourceUnit.getName();
        final int lastSlashIndex = sourceUnitFullName.lastIndexOf(File.separator);
        final String sourceUnitFileName = sourceUnitFullName.substring(lastSlashIndex + 1);
        if (sourceUnitFileName.endsWith(".wvc")) {
            final var compileUnit = new DefaultWebViewComponentTemplateCompileUnit(
                    sourceUnitFileName,
                    AnonymousWebViewComponent.class,
                    ComponentTemplateSource.of(sourceUnit.getSource().getURI()),
                    "" // default package
            );

            final CompilationUnitNode cuNode;
            try {
                cuNode = CompilerPipeline.parseAndBuildAst(compileUnit);
            } catch (WebViewComponentTemplateCompileException | MultipleWebViewComponentCompileErrorsException e) {
                this.logException(e);
                throw this.translateException(e);
            }

            final var groovyTranspiler = new DefaultGroovyTranspiler();
            final String teplateClassSimpleName = sourceUnitFileName.substring(0, sourceUnitFileName.length() - 4);
            try {
                final SourceUnit transpiledSourceUnit = groovyTranspiler.transpile(
                        new DefaultComponentTemplateCompilerConfiguration(),
                        compileUnit,
                        cuNode,
                        teplateClassSimpleName
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
