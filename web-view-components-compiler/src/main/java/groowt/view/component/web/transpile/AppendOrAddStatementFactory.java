package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.BodyChildNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.function.Function;

public interface AppendOrAddStatementFactory {

    enum Action {
        ADD, APPEND
    }

    Statement addOrAppend(BodyChildNode sourceNode, TranspilerState state, Function<Action, Expression> getRightSide);

    default Statement addOrAppend(BodyChildNode sourceNode, TranspilerState state, Expression rightSide) {
        return this.addOrAppend(sourceNode, state, ignored -> rightSide);
    }

}
