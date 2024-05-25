package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.ValueNode;
import org.codehaus.groovy.ast.expr.Expression;

public interface ValueNodeTranspiler {

    Expression createExpression(
            ValueNode valueNode,
            TranspilerState state
    );

}
