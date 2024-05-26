package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.Node;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.Token;
import org.codehaus.groovy.ast.ASTNode;

public interface PositionSetter {

    /**
     * Sets the position of the Groovy node off-set from the start
     * of the start of the Wvc node. The formula is
     * thus:
     * <pre>
     * target.line = container.line + target.line - 1
     * target.column = target.line == 1 ? container.column + target.column - 1 : target.column
     * target.lastLine = container.line + target.lastLine - 1
     * target.lastColumn = target.lastLine == 1 ? container.column + target.lastColumn - 1 : target.lastColumn
     * </pre>
     * For example, if the container has 2,1..4,1 and the target has
     * 3,1..3,1 (in its source), the target will be adjusted to 4,1..4,1.
     *
     * @param target The (Groovy) node whose position is to be set.
     * @param container The containing (Wvc) node.
     */
    void setPositionOffsetInContainer(ASTNode target, Node container);

    void setPosition(ASTNode target, Token source);

    void setPosition(ASTNode target, TokenRange tokenRange);
    void setPosition(ASTNode target, Node source);

    @Deprecated
    void setPosition(ASTNode target, Node start, Node end);

    @Deprecated
    void setToStartOf(ASTNode target, Node source);

    PositionSetter withOffset(int lineOffset, int columnOffset);

}
