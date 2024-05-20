package groowt.view.component.web.ast.extension;

import groowt.view.component.web.ast.node.Node;

@FunctionalInterface
public interface NodeExtensionFactory {
    <E extends NodeExtension> E create(Class<E> extensionClass, Node self, Object... constructorArgs);
}
