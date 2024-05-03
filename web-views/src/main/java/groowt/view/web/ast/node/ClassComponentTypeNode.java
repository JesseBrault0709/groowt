package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.Objects;

public non-sealed class ClassComponentTypeNode extends ComponentTypeNode {

    private String fqn;

    @Inject
    public ClassComponentTypeNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange
    ) {
        super(tokenList, tokenRange, extensionContainer, tokenRange);
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fqn = Objects.requireNonNull(fullyQualifiedName);
    }

    public String getFullyQualifiedName() {
        return Objects.requireNonNullElse(this.fqn, this.getIdentifier());
    }

}
