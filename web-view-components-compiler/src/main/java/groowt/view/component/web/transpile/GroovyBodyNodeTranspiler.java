package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.GroovyBodyNode;
import org.codehaus.groovy.ast.stmt.Statement;

public interface GroovyBodyNodeTranspiler {
    Statement createGroovyBodyNodeStatements(GroovyBodyNode groovyBodyNode, TranspilerState state);
}
