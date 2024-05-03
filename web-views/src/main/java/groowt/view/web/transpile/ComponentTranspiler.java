package groowt.view.web.transpile;

import groowt.view.web.ast.node.ComponentNode;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;

public interface ComponentTranspiler {
    BlockStatement createComponentStatements(
            ComponentNode componentNode,
            TranspilerState state
    );
}
