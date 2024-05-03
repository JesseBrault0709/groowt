package groowt.util.di;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface KeyBinder<K> {
    Class<K> getKeyClass();
    <B extends KeyBinder<K>, T> void bind(KeyHolder<B, ? extends K, T> keyHolder, Consumer<? super BindingConfigurator<T>> configure);
    <B extends KeyBinder<K>, T> @Nullable Binding<T> getBinding(KeyHolder<B, ? extends K, T> keyHolder);
    <B extends KeyBinder<K>, T> void removeBinding(KeyHolder<B, ? extends K, T> keyHolder);
    <B extends KeyBinder<K>, T> void removeBindingIf(KeyHolder<B, ? extends K, T> keyHolder, Predicate<? super Binding<T>> filter);
    void clearAllBindings();
}
