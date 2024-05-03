package groowt.util.di;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultRegistry implements Registry {

    protected record ClassKeyBinding<T>(Class<T> key, Binding<T> binding) {}

    protected final Collection<ClassKeyBinding<?>> classBindings = new ArrayList<>();
    protected final Collection<RegistryExtension> extensions = new ArrayList<>();

    @Override
    public void removeBinding(Class<?> key) {
        this.classBindings.removeIf(classKeyBinding -> classKeyBinding.key().equals(key));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void removeBindingIf(Class<T> key, Predicate<Binding<T>> filter) {
        this.classBindings.removeIf(classKeyBinding ->
                classKeyBinding.key().equals(key) && filter.test((Binding<T>) classKeyBinding.binding())
        );
    }

    private <E extends RegistryExtension> List<E> getAllRegistryExtensions(Class<E> extensionType) {
        return this.extensions.stream()
                .filter(extension -> extensionType.isAssignableFrom(extension.getClass()))
                .map(extensionType::cast)
                .toList();
    }

    private <E extends RegistryExtension> E getOneRegistryExtension(Class<E> extensionType) {
        final List<E> extensions = this.getAllRegistryExtensions(extensionType);
        if (extensions.size() == 1) {
            return extensions.getFirst();
        } else if (extensions.isEmpty()) {
            throw new IllegalArgumentException("There is no " + extensionType + " registered for this " + this);
        } else {
            throw new IllegalArgumentException("There is more than one " + extensionType + " registered for this " + this);
        }
    }

    @Override
    public void addExtension(RegistryExtension extension) {
        final List<? extends RegistryExtension> existing = this.getAllRegistryExtensions(extension.getClass());
        if (existing.isEmpty()) {
            this.extensions.add(extension);
        } else {
            throw new IllegalArgumentException("There is already at least one " + extension.getClass() + " registered in " + this);
        }
    }

    @Override
    public <E extends RegistryExtension> E getExtension(Class<E> extensionType) {
        return this.getOneRegistryExtension(extensionType);
    }

    @Override
    public <E extends RegistryExtension> Collection<E> getExtensions(Class<E> extensionType) {
        return this.getAllRegistryExtensions(extensionType);
    }

    @Override
    public void removeExtension(RegistryExtension extension) {
        this.extensions.remove(extension);
    }

    @Override
    public @Nullable <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> qualifierType) {
        final List<QualifierHandler<A>> handlers = new ArrayList<>();
        for (final var extension : this.extensions) {
            if (extension instanceof QualifierHandlerContainer handlerContainer) {
                final var handler = handlerContainer.getQualifierHandler(qualifierType);
                if (handler != null) {
                    handlers.add(handler);
                }
            }
        }
        if (handlers.isEmpty()) {
            return null;
        } else if (handlers.size() > 1) {
            throw new RuntimeException("There is more than one QualifierHandler for " + qualifierType.getName());
        } else {
            return handlers.getFirst();
        }
    }

    @Override
    public @Nullable <A extends Annotation> ScopeHandler<A> getScopeHandler(Class<A> scopeType) {
        final List<ScopeHandler<A>> handlers = new ArrayList<>();
        for (final var extension : this.extensions) {
            if (extension instanceof ScopeHandlerContainer handlerContainer) {
                final var handler = handlerContainer.getScopeHandler(scopeType);
                if (handler != null) {
                    handlers.add(handler);
                }
            }
        }
        if (handlers.isEmpty()) {
            return null;
        } else if (handlers.size() > 1) {
            throw new RuntimeException("There is more than one ScopeHandler for " + scopeType.getName());
        } else {
            return handlers.getFirst();
        }
    }

    @Override
    public <T> void bind(Class<T> key, Consumer<? super BindingConfigurator<T>> configure) {
        final var configurator = new SimpleBindingConfigurator<>(key);
        configure.accept(configurator);
        this.classBindings.add(new ClassKeyBinding<>(key, configurator.getBinding()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> Binding<T> getBinding(Class<T> key) {
        for (final var classKeyBinding : this.classBindings) {
            if (key.isAssignableFrom(classKeyBinding.key())) {
                return (Binding<T>) classKeyBinding.binding();
            }
        }
        return null;
    }

    private KeyBinder<?> findKeyBinder(Class<?> keyClass) {
        final List<KeyBinder<?>> binders = new ArrayList<>();
        for (final var extension : this.extensions) {
            if (extension instanceof KeyBinder<?> keyBinder && keyBinder.getKeyClass().isAssignableFrom(keyClass)) {
                binders.add(keyBinder);
            }
        }
        if (binders.isEmpty()) {
            throw new IllegalArgumentException("There are no configured RegistryExtensions that can handle keys with type " + keyClass.getName());
        } else if (binders.size() > 1) {
            throw new IllegalArgumentException("There is more than one configured RegistryExtension that can handle keys with type " + keyClass.getName());
        } else {
            return binders.getFirst();
        }
    }

    @SuppressWarnings("rawtypes")
    protected final void withKeyBinder(KeyHolder<?, ?, ?> keyHolder, Consumer<KeyBinder> action) {
        action.accept(this.findKeyBinder(keyHolder.key().getClass()));
    }

    @SuppressWarnings("rawtypes")
    protected final <R> @Nullable R tapKeyBinder(
            KeyHolder<?, ?, ?> keyHolder,
            Function<KeyBinder, @Nullable R> function
    ) {
        return function.apply(this.findKeyBinder(keyHolder.key().getClass()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void bind(KeyHolder<?, ?, T> keyHolder, Consumer<? super BindingConfigurator<T>> configure) {
        this.withKeyBinder(keyHolder, b -> b.bind(keyHolder, configure));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> Binding<T> getBinding(KeyHolder<?, ?, T> keyHolder) {
        return this.tapKeyBinder(keyHolder, b -> b.getBinding(keyHolder));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void removeBinding(KeyHolder<?, ?, T> keyHolder) {
        this.withKeyBinder(keyHolder, b -> b.removeBinding(keyHolder));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void removeBindingIf(
            KeyHolder<?, ?, T> keyHolder,
            Predicate<? super Binding<T>> filter
    ) {
        this.withKeyBinder(keyHolder, b -> b.removeBindingIf(keyHolder, filter));
    }

    @Override
    public void clearAllBindings() {
        this.classBindings.clear();
        for (final var extension : this.extensions) {
            if (extension instanceof KeyBinder<?> keyBinder) {
                keyBinder.clearAllBindings();
            }
        }
    }

}
