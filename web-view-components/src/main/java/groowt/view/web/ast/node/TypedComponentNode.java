package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

public non-sealed class TypedComponentNode extends ComponentNode {

    private final ComponentArgsNode args;

    @Inject
    public TypedComponentNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given ComponentArgsNode args,
            @Given @Nullable BodyNode children
    ) {
        super(tokenRange, extensionContainer, filterNulls(args, children), children);
        this.args = args;
    }

    public ComponentArgsNode getArgs() {
        return this.args;
    }

}
