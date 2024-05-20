package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.Node;
import groowt.view.component.web.util.TokenRange;
import org.codehaus.groovy.ast.ASTNode;

public interface PositionSetter {
    void setPosition(ASTNode target, TokenRange tokenRange);
    void setPosition(ASTNode target, Node source);
    void setPosition(ASTNode target, Node start, Node end);
    void setToStartOf(ASTNode target, Node source);
}
