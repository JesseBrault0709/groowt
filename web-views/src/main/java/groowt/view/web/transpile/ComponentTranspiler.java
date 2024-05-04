package groowt.view.web.transpile;

import groowt.view.web.ast.node.ComponentNode;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.stmt.BlockStatement;

public interface ComponentTranspiler {
    BlockStatement createComponentStatements(
            ComponentNode componentNode,
            TranspilerState state
    );
}
