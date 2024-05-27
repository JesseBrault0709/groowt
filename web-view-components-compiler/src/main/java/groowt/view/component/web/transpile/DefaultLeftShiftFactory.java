package groowt.view.component.web.transpile;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

public class DefaultLeftShiftFactory implements LeftShiftFactory {

    @Override
    public Statement create(TranspilerState state, Expression rightSide) {
        final Expression left;
        if (state.hasCurrentChildList()) {
            left = state.getCurrentChildList();
        } else {
            left = state.getWriter();
        }
        final BinaryExpression leftShift = new BinaryExpression(
                left,
                new Token(Types.LEFT_SHIFT, "<<", -1, -1),
                rightSide
        );
        return new ExpressionStatement(leftShift);
    }

}
