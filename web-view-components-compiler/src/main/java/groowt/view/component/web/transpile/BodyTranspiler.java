package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.BodyChildNode;
import groowt.view.component.web.ast.node.BodyNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;

public interface BodyTranspiler {

    @FunctionalInterface
    interface AddOrAppendCallback {
        Statement createStatement(BodyChildNode source, Expression expression);
    }

    BlockStatement transpileBody(
            BodyNode bodyNode,
            AddOrAppendCallback addOrAppendCallback,
            TranspilerState state
    );

}
