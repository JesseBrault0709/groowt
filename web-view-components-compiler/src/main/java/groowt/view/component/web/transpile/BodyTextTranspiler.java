package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.BodyTextNode;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.List;

public interface BodyTextTranspiler {
    List<Statement> createBodyTextStatements(BodyTextNode bodyTextNode, TranspilerState state);
}
