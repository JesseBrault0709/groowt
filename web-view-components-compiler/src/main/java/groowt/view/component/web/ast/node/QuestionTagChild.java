package groowt.view.component.web.ast.node;

public interface QuestionTagChild {

    default Node asNode() {
        return (Node) this;
    }

}
