package groowt.view.component.web.transpile;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.*;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.Token;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;

import static groowt.view.component.web.transpile.TranspilerUtil.getStringLiteral;

public class DefaultBodyTextTranspiler implements BodyTextTranspiler {

    private final GroovyBodyNodeTranspiler groovyBodyNodeTranspiler;
    private final PositionSetter positionSetter;
    private final LeftShiftFactory leftShiftFactory;
    private final boolean includeComments;

    @Inject
    public DefaultBodyTextTranspiler(
            GroovyBodyNodeTranspiler groovyBodyNodeTranspiler,
            PositionSetter positionSetter,
            LeftShiftFactory leftShiftFactory,
            @Given boolean includeComments
    ) {
        this.groovyBodyNodeTranspiler = groovyBodyNodeTranspiler;
        this.positionSetter = positionSetter;
        this.leftShiftFactory = leftShiftFactory;
        this.includeComments = includeComments;
    }

    protected Statement handleStringLiteral(Token source) {
        final ConstantExpression literal = getStringLiteral(source.getText());
        this.positionSetter.setPosition(literal, source);
        return this.leftShiftFactory.create(literal);
    }

    protected Statement handleStringLiteral(Node source, String content) {
        final ConstantExpression literal = getStringLiteral(content);
        this.positionSetter.setPosition(literal, source);
        return this.leftShiftFactory.create(literal);
    }

    protected List<Statement> handleHtmlCommentChild(HtmlCommentChild child, TranspilerState state) {
        return switch (child) {
            case BodyTextChild bodyTextChild -> this.handleBodyTextChild(bodyTextChild, state);
            default -> throw new WebViewComponentBugError(new UnsupportedOperationException(
                    "Unsupported HtmlCommentChild type " + child.getClass().getName()
            ));
        };
    }

    protected List<Statement> handleQuestionTagChild(QuestionTagChild child, TranspilerState state) {
        return switch (child) {
            case BodyTextChild bodyTextChild -> this.handleBodyTextChild(bodyTextChild, state);
            default -> throw new WebViewComponentBugError(new UnsupportedOperationException(
                    "Unsupported QuestionTagChild type " + child.getClass().getName()
            ));
        };
    }

    protected List<Statement> handleBodyTextChild(BodyTextChild child, TranspilerState state) {
        final List<Statement> result = new ArrayList<>();
        switch (child) {
            case QuestionNode questionNode -> {
                result.add(this.handleStringLiteral(questionNode.getOpenToken()));
                questionNode.getChildrenAsQuestionTagChildren().stream()
                        .map(questionChild -> this.handleQuestionTagChild(questionChild, state))
                        .forEach(result::addAll);
                result.add(this.handleStringLiteral(questionNode.getCloseToken()));
            }
            case HtmlCommentNode commentNode -> {
                if (this.includeComments) {
                    result.add(this.handleStringLiteral(commentNode.getOpenToken()));
                    commentNode.getChildrenAsHtmlCommentChildren().stream()
                            .map(commentChild -> this.handleHtmlCommentChild(commentChild, state))
                            .forEach(result::addAll);
                    result.add(this.handleStringLiteral(commentNode.getCloseToken()));
                }
            }
            case TextNode textNode -> {
                result.add(this.handleStringLiteral(textNode, textNode.getContent()));
            }
            case GroovyBodyNode groovyBodyNode -> {
                result.add(this.groovyBodyNodeTranspiler.createGroovyBodyNodeStatements(groovyBodyNode, state));
            }
            default -> throw new WebViewComponentBugError(new UnsupportedOperationException(
                    "BodyTextChild of type " + child.getClass().getName() + " is not supported."
            ));
        }
        return result;
    }

    @Override
    public List<Statement> createBodyTextStatements(BodyTextNode bodyTextNode, TranspilerState state) {
        final List<Statement> result = new ArrayList<>();
        for (final BodyTextChild child : bodyTextNode.getChildrenAsBodyTextChildren()) {
            result.addAll(this.handleBodyTextChild(child, state));
        }
        return result;
    }

}
