package groowt.view.component.web.compiler;

import groowt.view.component.compiler.*;
import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import groowt.view.component.web.transpile.DefaultGroovyTranspiler;
import org.antlr.v4.runtime.ParserRuleContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.tools.GroovyClass;

import java.util.HashSet;
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

    @Override
    protected ComponentTemplateCompileResult doCompile(WebViewComponentTemplateCompileUnit compileUnit)
            throws ComponentTemplateCompileException {

        final CompilationUnitNode cuNode = CompilerPipeline.parseAndBuildAst(compileUnit);

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
