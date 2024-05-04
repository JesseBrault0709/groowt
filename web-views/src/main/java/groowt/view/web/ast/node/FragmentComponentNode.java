package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;

public non-sealed class FragmentComponentNode extends ComponentNode {

    @Inject
    public FragmentComponentNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given BodyNode body
    ) {
        super(tokenRange, extensionContainer, List.of(Objects.requireNonNull(body)), body);
    }

    @Override
    public BodyNode getBody() {
        return Objects.requireNonNull(super.getBody());
    }

}
