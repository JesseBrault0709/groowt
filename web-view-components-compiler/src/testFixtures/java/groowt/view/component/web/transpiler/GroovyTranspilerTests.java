package groowt.view.component.web.transpiler;

import groovy.lang.Tuple2;
import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.DefaultComponentTemplateCompilerConfiguration;
import groowt.view.component.compiler.source.StringSource;
import groowt.view.component.web.BaseWebViewComponent;
import groowt.view.component.web.antlr.ParserUtil;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.DefaultAstBuilder;
import groowt.view.component.web.ast.DefaultNodeFactory;
import groowt.view.component.web.compiler.AnonymousWebViewComponent;
import groowt.view.component.web.compiler.DefaultWebViewComponentTemplateCompileUnit;
import groowt.view.component.web.transpile.GroovyTranspiler;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static groowt.view.component.web.antlr.LexerErrorKt.formatLexerError;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public abstract class GroovyTranspilerTests {

    private static final Logger logger = LoggerFactory.getLogger(GroovyTranspilerTests.class);

    protected final GroovyTranspiler transpiler;
    private final CompilationUnit groovyCompilationUnit;

    public GroovyTranspilerTests(Tuple2<GroovyTranspiler, CompilationUnit> params) {
        this.transpiler = params.getV1();
        this.groovyCompilationUnit = params.getV2();
    }

    @Test
    public void smokeScreen() {}

    private void doTranspile(String source) {
        final var parseResult = ParserUtil.parseCompilationUnit(source);

        if (!parseResult.getLexerErrors().isEmpty()) {
            logger.error("There were lexer errors.");
            parseResult.getLexerErrors().forEach(error -> {
                logger.error(formatLexerError(error));
            });
            fail("There were lexer errors. See log for more information.");
        }

        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        final var cuNode = astBuilder.buildCompilationUnit(parseResult.getCompilationUnitContext());
        try {
            this.transpiler.transpile(
                    new DefaultComponentTemplateCompilerConfiguration(),
                    new DefaultWebViewComponentTemplateCompileUnit(
                            "<anonymous string source>",
                            AnonymousWebViewComponent.class,
                            new StringSource(source, null),
                            "groowt.view.component.web.transpiler"
                    ),
                    cuNode,
                    "Template" + source.hashCode()
            );
        } catch (ComponentTemplateCompileException e) {
            fail(e);
        }

        assertDoesNotThrow(() -> {
            this.groovyCompilationUnit.compile(CompilePhase.CLASS_GENERATION.getPhaseNumber());
        });
    }

    /**
     * Absolute <strong><em>woot!</em></strong> 4/30/24
     */
    @Test
    public void helloTarget() {
        this.doTranspile("Hello, $target!");
    }

    public static final class Greeter extends BaseWebViewComponent {}

    @Test
    public void withComponent() {
        this.doTranspile("<GroovyTranspilerTests.Greeter target='World' />");
    }

    @Test
    public void withPackageInPreamble() {
        this.doTranspile("""
                ---
                package test
                ---
                Hello, World!
                """.stripIndent().trim());
    }

}
