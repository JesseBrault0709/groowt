package groowt.util.di;

import jakarta.inject.Provider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SimpleBindingConfigurator<T> implements BindingConfigurator<T> {

    private final Class<T> from;
    private @Nullable Binding<T> binding;

    public SimpleBindingConfigurator(Class<T> from) {
        this.from = from;
    }

    public final Binding<T> getBinding() {
        return this.binding != null
                ? this.binding
                : new ClassBinding<>(this.from, this.from); // return SelfBinding in case we never called anything
    }

    @Override
    public void to(Class<? extends T> target) {
        this.binding = new ClassBinding<>(this.from, target);
    }

    @Override
    public void toProvider(Provider<? extends T> provider) {
        this.binding = new ProviderBinding<>(this.from, provider);
    }

    @Override
    public void toSingleton(T target) {
        this.binding = new SingletonBinding<>(target);
    }

    @Override
    public void toLazySingleton(Supplier<T> singletonSupplier) {
        this.binding = new LazySingletonBinding<>(singletonSupplier);
    }

}
