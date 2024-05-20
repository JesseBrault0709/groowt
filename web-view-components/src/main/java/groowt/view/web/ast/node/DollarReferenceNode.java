package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.GStringPathExtension;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

public class DollarReferenceNode extends AbstractLeafNode {

    private final int groovyTokenIndex;

    @Inject
    public DollarReferenceNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int groovyTokenIndex
    ) {
        super(tokenRange, extensionContainer);
        this.groovyTokenIndex = groovyTokenIndex;
        this.createGStringPath(tokenList);
    }

    protected void createGStringPath(TokenList tokenList) {
        this.createExtension(GStringPathExtension.class, TokenRange.fromIndex(tokenList, this.groovyTokenIndex));
    }

    public GStringPathExtension getGStringPath() {
        return this.getExtension(GStringPathExtension.class);
    }

}
