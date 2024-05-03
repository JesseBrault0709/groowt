package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

public class KeyNode extends AbstractLeafNode {

    private final String key;

    @Inject
    public KeyNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int keyTokenIndex
    ) {
        super(tokenRange, extensionContainer);
        this.key = tokenList.get(keyTokenIndex).getText();
    }

    public String getKey() {
        return this.key;
    }

}
