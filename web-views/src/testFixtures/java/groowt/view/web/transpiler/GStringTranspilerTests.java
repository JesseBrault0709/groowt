package groowt.view.web.transpiler;

import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.ast.node.GStringBodyTextNode;
import groowt.view.web.transpile.GStringTranspiler;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class GStringTranspilerTests {

    protected abstract GStringTranspiler getGStringTranspiler();

    @Test
    public void smokeScreen() {
        assertDoesNotThrow(() -> {
            getGStringTranspiler();
        });
    }

    @Test
    public void gStringExpressionWithDollarReference() {
        final var source = "Hello, $target!";
        final var parseResult = ParserUtil.parseCompilationUnit(source);
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var nodeFactory = new DefaultNodeFactory(tokenList);
        final var astBuilder = new DefaultAstBuilder(nodeFactory);
        final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());
        final var bodyNode = cuNode.getBodyNode();
        assertNotNull(bodyNode);
        final var gStringBodyTextNode = bodyNode.getAt(0, GStringBodyTextNode.class);
        final var transpiler = this.getGStringTranspiler();
        final GStringExpression gStringExpression = transpiler.createGStringExpression(gStringBodyTextNode);
        assertEquals("Hello, $target!", gStringExpression.getText());
    }

}
