package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.ComponentNode;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.List;

public interface ComponentTranspiler {
    List<Statement> createComponentStatements(ComponentNode componentNode, TranspilerState state, boolean isAttrComponent);
    List<Statement> createComponentStatements(ComponentNode componentNode, TranspilerState state);
}
