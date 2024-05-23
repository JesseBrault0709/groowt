package groowt.view.component.web.ast.node;

public interface HtmlCommentChild {

    default Node asNode() {
        return (Node) this;
    }

}
