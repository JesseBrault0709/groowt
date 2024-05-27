package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.BodyNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;

public interface BodyTranspiler {
    BlockStatement transpileBody(BodyNode bodyNode, TranspilerState state);
}
