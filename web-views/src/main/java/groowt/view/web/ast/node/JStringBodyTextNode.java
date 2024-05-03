package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

public class JStringBodyTextNode extends AbstractLeafNode implements BodyChildNode {

    private final String content;

    @Inject
    public JStringBodyTextNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given String content
    ) {
        super(tokenRange, extensionContainer);
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

}
