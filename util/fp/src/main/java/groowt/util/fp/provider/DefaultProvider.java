package groowt.util.fp.provider;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultProvider<T> implements Provider<T> {

    public static <T> Provider<T> of(T t) {
        Objects.requireNonNull(t);
        return new DefaultProvider<>(t);
    }

    public static <T> Provider<T> ofLazy(Class<T> type, Supplier<? extends T> tSupplier) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(tSupplier);
        return new DefaultProvider<>(type, tSupplier);
    }

    private final Class<T> type;
    private final Supplier<T> tSupplier;

    @SuppressWarnings("unchecked")
    protected DefaultProvider(T t) {
        this.tSupplier = () -> t;
        this.type = (Class<T>) t.getClass();
    }

    protected DefaultProvider(Class<T> type, Supplier<? extends T> tSupplier) {
        this.type = type;
        this.tSupplier = tSupplier::get;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public T get() {
        return Objects.requireNonNull(this.tSupplier.get());
    }

}
