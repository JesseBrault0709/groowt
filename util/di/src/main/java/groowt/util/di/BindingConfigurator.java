package groowt.util.di;

import jakarta.inject.Provider;

import java.util.function.Supplier;

public interface BindingConfigurator<T> {
    void to(Class<? extends T> target);
    void toProvider(Provider<? extends T> provider);
    void toSingleton(T target);
    void toLazySingleton(Supplier<T> singletonSupplier);
}
