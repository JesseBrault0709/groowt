package groowt.view.web.ast.extension;

import groowt.util.di.RegistryObjectFactory;
import groowt.view.web.ast.node.Node;
import jakarta.inject.Inject;

public final class SimpleNodeExtensionFactory implements NodeExtensionFactory {

    private final RegistryObjectFactory objectFactory;

    @Inject
    public SimpleNodeExtensionFactory(RegistryObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public <E extends NodeExtension> E create(Class<E> extensionClass, Node self, Object... constructorArgs) {
        this.objectFactory.configureRegistry(r -> {
            r.getExtension(SelfNodeRegistryExtension.class).setSelfNode(self);
        });
        try {
            return this.objectFactory.createInstance(extensionClass, constructorArgs);
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not create " + extensionClass + "\n" + e.getMessage(), e);
        } finally {
            this.objectFactory.configureRegistry(r -> {
                r.getExtension(SelfNodeRegistryExtension.class).setSelfNode(null);
            });
        }
    }

}