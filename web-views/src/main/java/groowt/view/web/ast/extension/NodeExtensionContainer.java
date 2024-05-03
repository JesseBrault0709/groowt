package groowt.view.web.ast.extension;

import groowt.util.extensible.ExtensionContainer;
import groowt.view.web.ast.node.Node;

public interface NodeExtensionContainer extends ExtensionContainer<NodeExtension, NodeExtensionFactory> {
    <E extends NodeExtension> E createExtension(Class<E> extensionClass, Node self, Object... givenArgs);
}
