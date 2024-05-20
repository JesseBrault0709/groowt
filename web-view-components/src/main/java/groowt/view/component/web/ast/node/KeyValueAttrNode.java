package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

public non-sealed class KeyValueAttrNode extends AttrNode {

    private final ValueNode valueNode;

    @Inject
    public KeyValueAttrNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given KeyNode keyNode,
            @Given ValueNode valueNode
    ) {
        super(tokenRange, extensionContainer, checkForNulls(keyNode, valueNode.asNode()), keyNode);
        this.valueNode = valueNode;
    }

    public ValueNode getValueNode() {
        return this.valueNode;
    }

}
