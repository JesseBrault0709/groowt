package groowt.util.extensible;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

// TODO: groovy methods to handle getting extensions via property accessors
public interface Extensible<E, F, C extends ExtensionContainer<E, F>> extends ExtensionAware<E> {
    <T extends E> T createExtension(Class<T> extensionClass, Object... constructorArgs);
    C getExtensionContainer();
}
