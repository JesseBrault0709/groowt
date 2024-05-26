package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;

public class BodyTextNode extends AbstractTreeNode implements BodyChildNode {

    protected static List<? extends BodyTextChild> checkChildren(List<? extends BodyTextChild> children) {
        if (children.isEmpty()) {
            throw new IllegalArgumentException("A valid BodyTextNode must have at least one child BodyTextChildNode.");
        }
        return children;
    }

    protected static List<Node> childrenAsNodes(List<? extends BodyTextChild> children) {
        return children.stream().map(BodyTextChild::asNode).toList();
    }

    @Inject
    public BodyTextNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given List<? extends BodyTextChild> children
    ) {
        super(tokenRange, extensionContainer, childrenAsNodes(checkChildren(children)));
    }

    public List<BodyTextChild> getChildrenAsBodyTextChildren() {
        return this.getChildren().stream().map(BodyTextChild.class::cast).toList();
    }

}
