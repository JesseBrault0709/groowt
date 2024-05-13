package groowt.util.fp.provider;

import java.util.Objects;

final class SimpleProvider<T> implements Provider<T> {

    private final T t;

    public SimpleProvider(T t) {
        this.t = Objects.requireNonNull(t);
    }

    @Override
    public T get() {
        return t;
    }

}
