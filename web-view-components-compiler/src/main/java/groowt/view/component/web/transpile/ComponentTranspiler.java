package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.ComponentNode;
import groowt.view.component.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.List;

public interface ComponentTranspiler {
    List<Statement> createComponentStatements(
            ComponentNode componentNode,
            TranspilerState state
    );
}
