package groowt.view.web.transpile;

import groowt.view.web.transpile.util.GroovyUtil;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.syntax.Types;

import static org.codehaus.groovy.syntax.Token.newSymbol;

public class SimpleOutStatementFactory implements OutStatementFactory {

    @Override
    public Statement create(Expression rightSide) {
        final VariableExpression out = new VariableExpression("out");
        final MethodCallExpression methodCallExpression = new MethodCallExpression(
                out,
                "append",
                rightSide
        );
        return new ExpressionStatement(methodCallExpression);
    }

}
