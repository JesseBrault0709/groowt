package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

public class CompilationUnitNode extends AbstractTreeNode {

    private final @Nullable PreambleNode preambleNode;
    private final @Nullable BodyNode bodyNode;

    @Inject
    public CompilationUnitNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given @Nullable PreambleNode preambleNode,
            @Given @Nullable BodyNode bodyNode
    ) {
        super(tokenRange, extensionContainer, filterNulls(preambleNode, bodyNode));
        this.preambleNode = preambleNode;
        this.bodyNode = bodyNode;
    }

    public @Nullable PreambleNode getPreambleNode() {
        return this.preambleNode;
    }

    public @Nullable BodyNode getBodyNode() {
        return this.bodyNode;
    }

}
