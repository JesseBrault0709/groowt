package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;

public non-sealed class BooleanValueAttrNode extends AttrNode {

    @Inject
    public BooleanValueAttrNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given KeyNode keyNode
    ) {
        super(tokenRange, extensionContainer, List.of(keyNode), keyNode);
    }

}
