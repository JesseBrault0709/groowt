package groowt.util.di;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Registry extends ExtensionContainer, QualifierHandlerContainer, ScopeHandlerContainer {
    <T> void bind(Class<T> key, Consumer<? super BindingConfigurator<T>> configure);
    @Nullable <T> Binding<T> getBinding(Class<T> key);
    void removeBinding(Class<?> key);
    <T> void removeBindingIf(Class<T> key, Predicate<Binding<T>> filter);

    <T> void bind(KeyHolder<?, ?, T> keyHolder, Consumer<? super BindingConfigurator<T>> configure);
    <T> @Nullable Binding<T> getBinding(KeyHolder<?, ?, T> keyHolder);
    <T> void removeBinding(KeyHolder<?, ?, T> keyHolder);
    <T> void removeBindingIf(KeyHolder<?, ?, T> keyHolder, Predicate<? super Binding<T>> filter);
    void clearAllBindings();

}
