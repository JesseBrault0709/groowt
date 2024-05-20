package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
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
