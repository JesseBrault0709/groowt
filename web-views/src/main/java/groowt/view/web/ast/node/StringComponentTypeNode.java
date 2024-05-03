package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

public non-sealed class StringComponentTypeNode extends ComponentTypeNode {

    @Inject
    public StringComponentTypeNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int typeTokenIndex
    ) {
        super(tokenList, tokenRange, extensionContainer, TokenRange.fromIndex(tokenList, typeTokenIndex));
    }

}
