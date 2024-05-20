package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ComponentArgsNode extends AbstractTreeNode {

    private static List<Node> createChildren(
            ComponentTypeNode type,
            @Nullable ComponentConstructorNode constructor,
            List<? extends AttrNode> attributes
    ) {
        final List<Node> children = new ArrayList<>(filterNulls(type, constructor));
        children.addAll(attributes);
        return children;
    }

    private final ComponentTypeNode type;
    private final @Nullable ComponentConstructorNode constructor;
    private final List<AttrNode> attributes = new ArrayList<>();

    @Inject
    public ComponentArgsNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given ComponentTypeNode type,
            @Given @Nullable ComponentConstructorNode constructor,
            @Given List<? extends AttrNode> attributes
    ) {
        super(tokenRange, extensionContainer, createChildren(type, constructor, attributes));
        this.type = type;
        this.constructor = constructor;
        this.attributes.addAll(attributes);
    }

    public ComponentTypeNode getType() {
        return this.type;
    }

    @Nullable
    public ComponentConstructorNode getConstructor() {
        return this.constructor;
    }

    public List<AttrNode> getAttributes() {
        return this.attributes;
    }

}
