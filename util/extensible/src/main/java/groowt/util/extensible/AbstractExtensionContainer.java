package groowt.util.extensible;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public abstract class AbstractExtensionContainer<E, F> implements ExtensionContainer<E, F> {

    private final F extensionFactory;
    private final Collection<E> extensions = new ArrayList<>();

    public AbstractExtensionContainer(F extensionFactory) {
        this.extensionFactory = extensionFactory;
    }

    /**
     * @return A <strong>copy</strong> of the registered extensions.
     */
    protected Collection<E> getRegisteredExtensions() {
        return new ArrayList<>(this.extensions);
    }

    protected void registerExtension(E extension) {
        this.extensions.add(extension);
    }

    @Override
    public <T extends E> @Nullable T findExtension(Class<T> extensionClass) {
        return this.extensions.stream()
                .filter(extensionClass::isInstance)
                .findFirst()
                .map(extensionClass::cast)
                .orElse(null);
    }

    /**
     * @implNote While this {@link AbstractExtensionContainer} calls
     * {@link #getExtension}, which will throw if there is no registered
     * extension, this method may be overridden to not use {@link #getExtension}
     * and instead implement custom handling logic to avoid throwing, etc.
     */
    @Override
    public <T extends E> void configureExtension(Class<T> extensionClass, Consumer<? super T> configure) {
        configure.accept(this.getExtension(extensionClass));
    }

    @Override
    public <T extends E> T getExtension(Class<T> extensionClass) {
        return this.extensions.stream()
                .filter(extensionClass::isInstance)
                .findFirst()
                .map(extensionClass::cast)
                .orElseThrow(() -> new IllegalArgumentException("There is no registered extension for " + extensionClass.getName()));
    }

    @Override
    public boolean hasExtension(Class<? extends E> extensionClass) {
        return this.extensions.stream().anyMatch(extensionClass::isInstance);
    }

    @Override
    public F getExtensionFactory() {
        return this.extensionFactory;
    }

}
