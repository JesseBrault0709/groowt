package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class QuestionNode extends AbstractTreeNode implements BodyTextChild {

    private final Token openToken;
    private final Token closeToken;

    @Inject
    public QuestionNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given Token openToken,
            @Given Token closeToken,
            @Given List<? extends QuestionTagChild> children
    ) {
        super(tokenRange, extensionContainer, children.stream().map(QuestionTagChild::asNode).toList());
        this.openToken = openToken;
        this.closeToken = closeToken;
    }

    public List<QuestionTagChild> getChildrenAsQuestionTagChildren() {
        return this.getChildren().stream().map(QuestionTagChild.class::cast).toList();
    }

    public Token getOpenToken() {
        return this.openToken;
    }

    public Token getCloseToken() {
        return this.closeToken;
    }

}
