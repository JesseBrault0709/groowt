package groowt.util.fp.provider;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Provider<T> {

    static <T> Provider<T> of(T t) {
        return new SimpleProvider<>(t);
    }

    static <T> Provider<T> ofLazy(Supplier<? extends T> tSupplier) {
        return new LazyProvider<>(tSupplier);
    }

    T get();

    default <U> Provider<U> map(Function<? super T, ? extends U> mapper) {
        return new LazyProvider<>(() -> mapper.apply(this.get()));
    }

    default <U> Provider<U> flatMap(Function<? super T, ? extends Provider<? extends U>> flatMapper) {
        return new LazyProvider<>(() -> flatMapper.apply(this.get()).get());
    }

}
