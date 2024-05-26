package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class HtmlCommentNode extends AbstractTreeNode implements BodyTextChild {

    private final Token openToken;
    private final Token closeToken;

    @Inject
    public HtmlCommentNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given Token openToken,
            @Given Token closeToken,
            @Given List<? extends HtmlCommentChild> children
    ) {
        super(tokenRange, extensionContainer, children.stream().map(HtmlCommentChild::asNode).toList());
        this.openToken = openToken;
        this.closeToken = closeToken;
    }

    public List<HtmlCommentChild> getChildrenAsHtmlCommentChildren() {
        return this.getChildren().stream().map(HtmlCommentChild.class::cast).toList();
    }

    public Token getOpenToken() {
        return this.openToken;
    }

    public Token getCloseToken() {
        return this.closeToken;
    }

}
