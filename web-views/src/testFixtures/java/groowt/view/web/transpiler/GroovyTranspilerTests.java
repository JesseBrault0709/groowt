package groowt.view.web.transpiler;

import groovy.lang.Tuple2;
import groowt.view.component.ComponentContext;
import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.transpile.GroovyTranspiler;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.io.StringReaderSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public abstract class GroovyTranspilerTests {

    protected final GroovyTranspiler transpiler;
    private final CompilationUnit groovyCompilationUnit;

    public GroovyTranspilerTests(Tuple2<GroovyTranspiler, CompilationUnit> params) {
        this.transpiler = params.getV1();
        this.groovyCompilationUnit = params.getV2();
    }

    @Test
    public void smokeScreen() {}

    private void doTranspile(
            String source,
            String ownerComponentName
    ) {
        final var parseResult = ParserUtil.parseCompilationUnit(source);
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());
        this.transpiler.transpile(
                cuNode,
                tokenList,
                ownerComponentName,
                new StringReaderSource(source, new CompilerConfiguration())
        );

        assertDoesNotThrow(() -> {
            this.groovyCompilationUnit.compile(CompilePhase.CLASS_GENERATION.getPhaseNumber());
        });
    }

    /**
     * Absolute <strong><em>woot!</em></strong> 4/30/24
     */
    @Test
    public void helloTarget(@Mock ComponentContext componentContext) {
        this.doTranspile(
                "Hello, $target!",
                "HelloTarget"
        );
    }

    @Test
    public void withComponent() {
        this.doTranspile("<Greeter target='World' />", "WithComponent");
    }

}
