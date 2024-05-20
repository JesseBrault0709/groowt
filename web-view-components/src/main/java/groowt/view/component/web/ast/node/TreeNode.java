package groowt.view.component.web.ast.node;

import java.util.List;

public non-sealed interface TreeNode extends Node {
    List<Node> getChildren();
    Node getAt(int index);
    <C extends Node> C getAt(int index, Class<C> childClass);
    int indexOf(Node child);
    int getChildrenSize();
}
