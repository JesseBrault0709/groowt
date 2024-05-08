package groowt.view.web.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

final class LazyProvider<T> implements Provider<T> {

    private final Provider<T> lazy;

    public LazyProvider(Supplier<T> supplier) {
        this.lazy = () -> {
            final @Nullable T t = supplier.get();
            if (t == null) {
                throw new NullPointerException("This Provider has a null value.");
            }
            return t;
        };
    }

    @Override
    public T get() {
        return this.lazy.get();
    }

}
