package groowt.util.di;

import jakarta.inject.Provider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class BindingUtil {

    public static <T> Consumer<BindingConfigurator<T>> toClass(Class<? extends T> clazz) {
        return bc -> bc.to(clazz);
    }

    public static <T> Consumer<BindingConfigurator<T>> toProvider(Provider<? extends T> provider) {
        return bc -> bc.toProvider(provider);
    }

    public static <T> Consumer<BindingConfigurator<T>> toSingleton(T singleton) {
        return bc -> bc.toSingleton(singleton);
    }

    public static <T> Consumer<BindingConfigurator<T>> toLazySingleton(Supplier<T> singletonSupplier) {
        return bc -> bc.toLazySingleton(singletonSupplier);
    }

    public static <T> Consumer<BindingConfigurator<T>> toSelf() {
        return bc -> {};
    }

    private BindingUtil() {}

}
