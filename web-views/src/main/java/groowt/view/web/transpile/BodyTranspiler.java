package groowt.view.web.transpile;

import groowt.view.web.ast.node.BodyNode;
import groowt.view.web.ast.node.Node;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.List;

public interface BodyTranspiler {

    @FunctionalInterface
    interface ExpressionStatementConverter {
        Statement createStatement(Node source, Expression expression);
    }

    BlockStatement transpileBody(
            BodyNode bodyNode,
            ExpressionStatementConverter converter,
            TranspilerState state
    );

}
