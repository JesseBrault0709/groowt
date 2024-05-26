package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;

public class EqualsScriptletNode extends AbstractLeafNode implements GroovyBodyNode {

    private final String groovyCode;

    public EqualsScriptletNode(
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
