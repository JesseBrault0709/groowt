package groowt.view.web.transpile;

import groowt.view.web.ast.node.ValueNode;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.expr.Expression;

public interface ValueNodeTranspiler {

    Expression createExpression(
            ValueNode valueNode,
            TranspilerState state
    );

}
