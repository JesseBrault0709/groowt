package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

public class PreambleNode extends AbstractLeafNode {

    private final int groovyIndex;

    @Inject
    public PreambleNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int groovyCodeIndex
    ) {
        super(tokenRange, extensionContainer);
        this.groovyIndex = groovyCodeIndex;
    }

    public int getGroovyCodeIndex() {
        return this.groovyIndex;
    }

}
