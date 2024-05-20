package groowt.view.web.transpile;

import groowt.view.web.ast.node.ComponentNode;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.List;

public interface ComponentTranspiler {
    List<Statement> createComponentStatements(
            ComponentNode componentNode,
            TranspilerState state
    );
}
