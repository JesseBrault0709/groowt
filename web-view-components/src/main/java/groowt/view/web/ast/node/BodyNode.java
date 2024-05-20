package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;

public class BodyNode extends AbstractTreeNode {

    protected static List<? extends BodyChildNode> checkChildren(List<? extends BodyChildNode> children) {
        if (children.isEmpty()) {
            throw new IllegalArgumentException("A valid BodyNode must have at least one child.");
        }
        return children;
    }

    protected static List<Node> childrenAsNodes(List<? extends BodyChildNode> children) {
        return children.stream().map(BodyChildNode::asNode).toList();
    }

    @Inject
    public BodyNode(NodeExtensionContainer extensionContainer, @Given TokenRange tokenRange, @Given List<? extends BodyChildNode> children) {
        super(tokenRange, extensionContainer, childrenAsNodes(checkChildren(children)));
    }

}
