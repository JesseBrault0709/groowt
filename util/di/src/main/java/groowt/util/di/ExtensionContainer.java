package groowt.util.di;

import java.util.Collection;

public interface ExtensionContainer {
    void addExtension(RegistryExtension extension);
    <E extends RegistryExtension> E getExtension(Class<E> extensionType);
    <E extends RegistryExtension> Collection<E> getExtensions(Class<E> extensionType);
    void removeExtension(RegistryExtension extension);
}
