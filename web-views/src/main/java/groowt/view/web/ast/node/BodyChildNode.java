package groowt.view.web.ast.node;

public interface BodyChildNode {

    default Node asNode() {
        return (Node) this;
    }

    default <T extends Node> T asNode(Class<? extends T> nodeType) {
        return nodeType.cast(this);
    }

}