package groowt.util.extensible;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ExtensionAware<E> {
    @Nullable <T extends E> T findExtension(Class<T> extensionClass);
    <T extends E> void configureExtension(Class<T> extensionClass, Consumer<? super T> configure);
    <T extends E> T getExtension(Class<T> extensionClass);
    boolean hasExtension(Class<? extends E> extensionClass);
}
