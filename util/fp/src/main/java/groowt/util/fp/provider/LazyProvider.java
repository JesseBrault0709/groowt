package groowt.util.fp.provider;

import java.util.function.Supplier;

final class LazyProvider<T> implements Provider<T> {

    private final Supplier<? extends T> lazy;

    public LazyProvider(Supplier<? extends T> supplier) {
        this.lazy = supplier;
    }

    @Override
    public T get() {
        return this.lazy.get();
    }

}
