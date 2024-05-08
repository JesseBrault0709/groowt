package groowt.view.web.util;

import java.util.function.Supplier;

public interface Provider<T> {

    static <T> Provider<T> of(T t) {
        return new SimpleProvider<>(t);
    }

    static <T> Provider<T> ofLazy(Supplier<T> tSupplier) {
        return new LazyProvider<>(tSupplier);
    }

    T get();

}
