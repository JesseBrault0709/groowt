package groowt.view.component.web.ast.node;

public interface BodyTextChild {

    default Node asNode() {
        return (Node) this;
    }

}
