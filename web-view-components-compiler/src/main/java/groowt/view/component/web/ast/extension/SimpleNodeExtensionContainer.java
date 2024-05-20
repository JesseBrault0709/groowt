package groowt.view.component.web.ast.extension;

import groowt.util.extensible.AbstractExtensionContainer;
import groowt.view.component.web.ast.node.Node;

public class SimpleNodeExtensionContainer extends AbstractExtensionContainer<NodeExtension, NodeExtensionFactory>
        implements NodeExtensionContainer {

    public SimpleNodeExtensionContainer(NodeExtensionFactory extensionFactory) {
        super(extensionFactory);
    }

    @Override
    public <E extends NodeExtension> E createExtension(Class<E> extensionClass, Node self, Object... givenArgs) {
        if (this.hasExtension(extensionClass)) {
            throw new IllegalArgumentException(
                    "There is already an extension registered of type " + extensionClass.getName()
            );
        }
        final E extension = this.getExtensionFactory().create(extensionClass, self, givenArgs);
        this.registerExtension(extension);
        return extension;
    }

}
