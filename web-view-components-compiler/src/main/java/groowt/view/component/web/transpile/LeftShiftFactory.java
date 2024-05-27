package groowt.view.component.web.transpile;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public interface LeftShiftFactory {
    Statement create(TranspilerState state, Expression rightSide);
}
