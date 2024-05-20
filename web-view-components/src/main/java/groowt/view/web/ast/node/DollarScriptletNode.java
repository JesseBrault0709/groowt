package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.GStringScriptletExtension;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

public class DollarScriptletNode extends AbstractLeafNode {

    private final GStringScriptletExtension gStringScriptlet;

    @Inject
    public DollarScriptletNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int groovyTokenIndex
    ) {
        super(tokenRange, extensionContainer);
        this.gStringScriptlet = this.createGStringScriptlet(tokenList, groovyTokenIndex);
    }

    protected GStringScriptletExtension createGStringScriptlet(TokenList tokenList, int groovyTokenIndex) {
        return this.createExtension(GStringScriptletExtension.class, TokenRange.fromIndex(tokenList, groovyTokenIndex));
    }

    public GStringScriptletExtension getGStringScriptlet() {
        return this.gStringScriptlet;
    }

}
