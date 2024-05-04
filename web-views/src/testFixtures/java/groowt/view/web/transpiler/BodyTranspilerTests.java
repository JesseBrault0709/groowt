package groowt.view.web.transpiler;

import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.BodyNode;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.transpile.BodyTranspiler;
import groowt.view.web.transpile.TranspilerConfiguration;
import groowt.view.web.transpile.TranspilerUtil;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BodyTranspilerTests {

    protected abstract TranspilerConfiguration getConfiguration();

    protected record BuildResult(BodyNode bodyNode, TokenList tokenList) {}

    protected BuildResult build(String source) {
        final var parseResult = ParserUtil.parseCompilationUnit(source);
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var b = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        final var cuNode = (CompilationUnitNode) b.build(parseResult.getCompilationUnitContext());
        final var bodyNode = cuNode.getBodyNode();
        if (bodyNode == null) {
            fail("No BodyNode was built for source: " + source);
        }
        return new BuildResult(bodyNode, tokenList);
    }

    protected BodyTranspiler getBodyTranspiler() {
        return this.getConfiguration().getBodyTranspiler();
    }

    @Test
    public void smokeScreen() {
        assertDoesNotThrow(() -> {
            this.getBodyTranspiler();
        });
    }

    @Test
    public void simpleGStringOutStatement() {
        final var source = "Hello, $target!";
        final var buildResult = this.build(source);
        final var transpiler = this.getBodyTranspiler();
        final var state = TranspilerUtil.TranspilerState.withDefaultRootScope();
        final var addOrAppend = this.getConfiguration().getAppendOrAddStatementFactory();
        final BlockStatement blockStatement = transpiler.transpileBody(
                buildResult.bodyNode(),
                (node, expression) -> addOrAppend.addOrAppend(node, state, ignored -> expression),
                TranspilerUtil.TranspilerState.withDefaultRootScope()
        );
        assertEquals(1, blockStatement.getStatements().size());
    }

    @Test
    public void simpleJStringOutStatement() {
        final var source = "Hello, World!";
        final var buildResult = this.build(source);
        final var transpiler = this.getBodyTranspiler();
        final var state = TranspilerUtil.TranspilerState.withDefaultRootScope();
        final var addOrAppend = this.getConfiguration().getAppendOrAddStatementFactory();
        final BlockStatement blockStatement = transpiler.transpileBody(
                buildResult.bodyNode(),
                (node, expression) -> addOrAppend.addOrAppend(node, state, ignored -> expression),
                TranspilerUtil.TranspilerState.withDefaultRootScope()
        );
        assertEquals(1, blockStatement.getStatements().size());
        final var s0 = (ExpressionStatement) blockStatement.getStatements().getFirst();
        final var binaryExpression = (MethodCallExpression) s0.getExpression();
        final var args = (TupleExpression) binaryExpression.getArguments();
        final var first = (ConstantExpression) args.getExpression(0);
        assertEquals("Hello, World!", first.getValue());
    }

}
