package groowt.view.web.transpile;

import groowt.view.web.ast.node.BodyChildNode;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.function.Function;

public interface AppendOrAddStatementFactory {

    enum Action {
        ADD, APPEND
    }

    Statement addOnly(BodyChildNode sourceNode, TranspilerState state, Expression rightSide);
    Statement appendOnly(BodyChildNode sourceNode, TranspilerState state, Expression rightSide);
    Statement addOrAppend(BodyChildNode sourceNode, TranspilerState state, Function<Action, Expression> getRightSide);

}
