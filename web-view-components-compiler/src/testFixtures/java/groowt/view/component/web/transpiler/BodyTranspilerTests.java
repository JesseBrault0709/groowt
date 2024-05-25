package groowt.view.component.web.transpiler;

import groowt.view.component.web.antlr.ParserUtil;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.DefaultAstBuilder;
import groowt.view.component.web.ast.DefaultNodeFactory;
import groowt.view.component.web.ast.node.BodyNode;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.component.web.transpile.BodyTranspiler;
import groowt.view.component.web.transpile.TranspilerConfiguration;
import groowt.view.component.web.transpile.TranspilerState;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public abstract class BodyTranspilerTests {

    protected abstract TranspilerConfiguration getConfiguration(
            WebViewComponentTemplateCompileUnit compileUnit,
            ModuleNode moduleNode
    );

    protected record BuildResult(BodyNode bodyNode, TokenList tokenList) {

    }

    protected BuildResult build(String source) {
        final var parseResult = ParserUtil.parseCompilationUnit(source);
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var b = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        final var cuNode = b.buildCompilationUnit(parseResult.getCompilationUnitContext());
        final var bodyNode = cuNode.getBodyNode();
        if (bodyNode == null) {
            fail("No BodyNode was built for source: " + source);
        }
        return new BuildResult(bodyNode, tokenList);
    }

    protected BodyTranspiler getBodyTranspiler(TranspilerConfiguration configuration) {
        return configuration.getBodyTranspiler();
    }

    @Test
    public void smokeScreen(@Mock WebViewComponentTemplateCompileUnit compileUnit, @Mock ModuleNode moduleNode) {
        assertDoesNotThrow(() -> {
            final var configuration = this.getConfiguration(compileUnit, moduleNode);
            this.getBodyTranspiler(configuration);
        });
    }

    @Test
    public void simpleGStringOutStatement(
            @Mock WebViewComponentTemplateCompileUnit compileUnit,
            @Mock ModuleNode moduleNode
    ) {
        final var source = "Hello, $target!";
        final var buildResult = this.build(source);
        final var configuration = this.getConfiguration(compileUnit, moduleNode);
        final var transpiler = this.getBodyTranspiler(configuration);
        final var state = TranspilerState.withDefaultRootScope();
        final var addOrAppend = configuration.getAppendOrAddStatementFactory();
        final BlockStatement blockStatement = transpiler.transpileBody(
                buildResult.bodyNode(),
                (node, expression) -> addOrAppend.addOrAppend(node, state, ignored -> expression),
                TranspilerState.withDefaultRootScope()
        );
        assertEquals(1, blockStatement.getStatements().size());
    }

    @Test
    public void simpleJStringOutStatement(
            @Mock WebViewComponentTemplateCompileUnit compileUnit,
            @Mock ModuleNode moduleNode
    ) {
        final var source = "Hello, World!";
        final var buildResult = this.build(source);
        final var configuration = this.getConfiguration(compileUnit, moduleNode);
        final var transpiler = this.getBodyTranspiler(configuration);
        final var state = TranspilerState.withDefaultRootScope();
        final var addOrAppend = configuration.getAppendOrAddStatementFactory();
        final BlockStatement blockStatement = transpiler.transpileBody(
                buildResult.bodyNode(),
                (node, expression) -> addOrAppend.addOrAppend(node, state, ignored -> expression),
                TranspilerState.withDefaultRootScope()
        );
        assertEquals(1, blockStatement.getStatements().size());
        final var s0 = (ExpressionStatement) blockStatement.getStatements().getFirst();
        final var binaryExpression = (MethodCallExpression) s0.getExpression();
        final var args = (TupleExpression) binaryExpression.getArguments();
        final var first = (ConstantExpression) args.getExpression(0);
        assertEquals("Hello, World!", first.getValue());
    }

}
