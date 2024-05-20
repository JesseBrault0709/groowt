package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.GStringNodeExtension;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;

public class GStringBodyTextNode extends AbstractTreeNode implements BodyChildNode {

    protected static List<? extends Node> checkChildren(List<? extends Node> children) {
        for (final var child : children) {
            if (!(child instanceof JStringBodyTextNode || child.hasExtension(GStringNodeExtension.class))) {
                throw new IllegalArgumentException(
                        "Children of GStringBodyTextNode must be either a JStringBodyTextNode, " +
                                "or have a GStringNodeExtension."
                );
            }
        }
        return children;
    }

    @Inject
    public GStringBodyTextNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given List<? extends Node> children
    ) {
        super(tokenRange, extensionContainer, checkChildren(children));
    }

}
