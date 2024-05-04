package groowt.view.web.transpile;

import groowt.view.web.ast.node.BodyChildNode;
import groowt.view.web.ast.node.BodyNode;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
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
