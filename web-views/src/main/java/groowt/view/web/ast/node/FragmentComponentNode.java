package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

public non-sealed class FragmentComponentNode extends ComponentNode {

    @Inject
    public FragmentComponentNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given @Nullable BodyNode body
    ) {
        super(tokenRange, extensionContainer, filterNulls(body), body);
    }

}
