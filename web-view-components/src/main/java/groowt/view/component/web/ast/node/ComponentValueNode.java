package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;

public class ComponentValueNode extends AbstractTreeNode implements ValueNode {

    private final ComponentNode componentNode;

    @Inject
    public ComponentValueNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given ComponentNode componentNode
    ) {
        super(tokenRange, extensionContainer, List.of(componentNode));
        this.componentNode = componentNode;
    }

    public ComponentNode getComponentNode() {
        return this.componentNode;
    }

}
