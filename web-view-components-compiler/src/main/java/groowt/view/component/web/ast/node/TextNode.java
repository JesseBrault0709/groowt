package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

public class TextNode extends AbstractLeafNode implements BodyTextChild, HtmlCommentChild, QuestionTagChild {

    private final String content;

    @Inject
    public TextNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given String content
    ) {
        super(tokenRange, extensionContainer);
        this.content = content;
    }

    @Override
    public Node asNode() {
        return this;
    }

    public String getContent() {
        return this.content;
    }

}
