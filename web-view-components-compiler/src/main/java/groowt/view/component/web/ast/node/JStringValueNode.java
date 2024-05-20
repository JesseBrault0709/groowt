package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

public class JStringValueNode extends AbstractLeafNode implements ValueNode {

    private final String content;

    @Inject
    public JStringValueNode(
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
