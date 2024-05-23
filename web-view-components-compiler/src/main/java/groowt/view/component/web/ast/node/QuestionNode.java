package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;

public class QuestionNode extends AbstractTreeNode implements BodyTextChild {

    @Inject
    public QuestionNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given List<? extends QuestionTagChild> children
    ) {
        super(tokenRange, extensionContainer, children.stream().map(QuestionTagChild::asNode).toList());
    }

}
