package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.extension.GStringPathExtension;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

public class DollarReferenceNode extends AbstractLeafNode implements GroovyBodyNode {

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
