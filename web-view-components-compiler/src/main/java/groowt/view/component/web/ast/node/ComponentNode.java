package groowt.view.component.web.ast.node;

import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed abstract class ComponentNode extends AbstractTreeNode implements BodyChildNode permits
        FragmentComponentNode,
        TypedComponentNode {

    private final BodyNode bodyNode;

    public ComponentNode(
            TokenRange tokenRange,
            NodeExtensionContainer extensionContainer,
            List<? extends Node> children,
            @Nullable BodyNode bodyNode
    ) {
        super(tokenRange, extensionContainer, children);
        this.bodyNode = bodyNode;
    }

    @Nullable
    public BodyNode getBody() {
        return this.bodyNode;
    }

}
