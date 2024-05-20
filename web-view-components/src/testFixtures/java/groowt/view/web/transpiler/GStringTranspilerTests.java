package groowt.view.web.transpiler;

import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.BodyNode;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.ast.node.GStringBodyTextNode;
import groowt.view.web.transpile.GStringTranspiler;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public abstract class GStringTranspilerTests {

    protected abstract GStringTranspiler getGStringTranspiler();

    @Test
    public void smokeScreen() {
        assertDoesNotThrow(() -> {
            getGStringTranspiler();
        });
    }

    protected BodyNode getBodyNode(String source) {
        final var parseResult = ParserUtil.parseCompilationUnit(source);
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var nodeFactory = new DefaultNodeFactory(tokenList);
        final var astBuilder = new DefaultAstBuilder(nodeFactory);
        final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());
        return Objects.requireNonNull(cuNode.getBodyNode());
    }

    protected void doTest(String source, Consumer<GStringExpression> further) {
        final var gStringBodyTextNode = this.getBodyNode(source).getAt(0, GStringBodyTextNode.class);
        final var transpiler = this.getGStringTranspiler();
        final GStringExpression gStringExpression = transpiler.createGStringExpression(gStringBodyTextNode);
        assertEquals(source, gStringExpression.getText());
        further.accept(gStringExpression);
    }

    @Test
    public void gStringExpressionWithDollarReference() {
        this.doTest("Hello, $target!", gStringExpression -> {
            assertEquals(2, gStringExpression.getStrings().size());
            assertEquals(1, gStringExpression.getValues().size());
        });
    }

    @Test
    public void multiplePathValues() {
        this.doTest("$greeting, $target!", gStringExpression -> {
            assertEquals(3, gStringExpression.getStrings().size());
            assertEquals(2, gStringExpression.getValues().size());
            final var firstValue = gStringExpression.getValue(0);
            assertInstanceOf(VariableExpression.class, firstValue);
            assertEquals("greeting", firstValue.getText());
            final var secondValue = gStringExpression.getValue(1);
            assertInstanceOf(VariableExpression.class, secondValue);
            assertEquals("target", secondValue.getText());
        });
    }

    @Test
    public void pathAndClosure() {
        this.doTest("$greeting, ${consume(out)}!", gStringExpression -> {
            assertEquals(3, gStringExpression.getStrings().size());
            assertEquals(2, gStringExpression.getValues().size());
            final var firstValue = gStringExpression.getValue(0);
            assertInstanceOf(VariableExpression.class, firstValue);
            assertEquals("greeting", firstValue.getText());
            assertInstanceOf(ClosureExpression.class, gStringExpression.getValue(1));
        });
    }

}
