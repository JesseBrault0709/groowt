package groowt.view.web.ast.node;

import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;

import java.util.List;

public abstract sealed class AttrNode extends AbstractTreeNode permits BooleanValueAttrNode, KeyValueAttrNode {

    private final KeyNode keyNode;

    public AttrNode(
            TokenRange tokenRange,
            NodeExtensionContainer extensionContainer,
            List<? extends Node> children,
            KeyNode keyNode
    ) {
        super(tokenRange, extensionContainer, children);
        this.keyNode = keyNode;
    }

    public KeyNode getKeyNode() {
        return this.keyNode;
    }

}
