package groowt.view.web.ast.extension;

import groowt.view.web.ast.node.Node;

@FunctionalInterface
public interface NodeExtensionFactory {
    <E extends NodeExtension> E create(Class<E> extensionClass, Node self, Object... constructorArgs);
}
