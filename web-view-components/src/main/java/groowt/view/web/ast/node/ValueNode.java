package groowt.view.web.ast.node;

public interface ValueNode {

    default Node asNode() {
        return (Node) this;
    }

    default <T extends Node> T asNode(Class<? extends T> nodeType) {
        return nodeType.cast(this);
    }

}
