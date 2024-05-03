package groowt.util.di;

import jakarta.inject.Named;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DefaultNamedRegistryExtension implements NamedRegistryExtension {

    protected static class NamedQualifierHandler implements QualifierHandler<Named> {

        private final DefaultNamedRegistryExtension extension;

        public NamedQualifierHandler(DefaultNamedRegistryExtension extension) {
            this.extension = extension;
        }

        @Override
        public <T> @Nullable Binding<T> handle(Named named, Class<T> dependencyClass) {
            return this.extension.getBinding(
                    new SimpleKeyHolder<>(NamedRegistryExtension.class, dependencyClass, named.value())
            );
        }

    }

    protected final Map<String, Binding<?>> bindings = new HashMap<>();
    protected final QualifierHandler<Named> qualifierHandler = this.getNamedQualifierHandler();

    protected QualifierHandler<Named> getNamedQualifierHandler() {
        return new NamedQualifierHandler(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> qualifierType) {
        return Named.class.equals(qualifierType) ? (QualifierHandler<A>) this.qualifierHandler : null;
    }

    @Override
    public Class<String> getKeyClass() {
        return String.class;
    }

    @Override
    public <B extends KeyBinder<String>, T> void bind(KeyHolder<B, ? extends String, T> keyHolder, Consumer<? super BindingConfigurator<T>> configure) {
        final var configurator = new SimpleBindingConfigurator<>(keyHolder.type());
        configure.accept(configurator);
        this.bindings.put(keyHolder.key(), configurator.getBinding());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <B extends KeyBinder<String>, T> Binding<T> getBinding(KeyHolder<B, ? extends String, T> keyHolder) {
        return (Binding<T>) this.bindings.getOrDefault(keyHolder.key(), null);
    }

    @Override
    public <B extends KeyBinder<String>, T> void removeBinding(KeyHolder<B, ? extends String, T> keyHolder) {
        this.bindings.remove(keyHolder.key());
    }

    @Override
    public <B extends KeyBinder<String>, T> void removeBindingIf(KeyHolder<B, ? extends String, T> keyHolder, Predicate<? super Binding<T>> filter) {
        final String key = keyHolder.key();
        if (this.bindings.containsKey(key) && filter.test(this.getBinding(keyHolder))) {
            this.bindings.remove(key);
        }
    }

    @Override
    public void clearAllBindings() {
        this.bindings.clear();
    }

}
