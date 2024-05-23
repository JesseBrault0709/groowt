package groowt.view.component.web.ast.node;

public interface GroovyBodyNode extends BodyTextChild, HtmlCommentChild, QuestionTagChild {

    @Override
    default Node asNode() {
        return (Node) this;
    }

}
