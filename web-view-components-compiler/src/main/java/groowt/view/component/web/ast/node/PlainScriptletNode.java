package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

public class PlainScriptletNode extends AbstractLeafNode implements GroovyBodyNode {

    private final String groovyCode;

    @Inject
    public PlainScriptletNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given String groovyCode
    ) {
        super(tokenRange, extensionContainer);
        this.groovyCode = groovyCode;
    }

    public String getGroovyCode() {
        return this.groovyCode;
    }

}
