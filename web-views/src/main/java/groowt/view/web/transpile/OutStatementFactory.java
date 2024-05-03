package groowt.view.web.transpile;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public interface OutStatementFactory {
    Statement create(Expression rightSide);
}
