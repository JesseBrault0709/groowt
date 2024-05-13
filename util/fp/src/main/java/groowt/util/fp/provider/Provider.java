package groowt.util.fp.provider;

import groowt.util.fp.hkt.SemiGroup;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @implSpec Must throw {@link NullPointerException} if the contained value is {@code null}
 * when either created or when retrieved.
 *
 * @param <T> The type of the value contained within.
 */
@FunctionalInterface
public interface Provider<T> {

    static <T> Provider<T> of(T t) {
        Objects.requireNonNull(t);
        return () -> t;
    }

    static <T> Provider<T> ofLazy(Supplier<? extends T> tSupplier) {
        Objects.requireNonNull(tSupplier);
        return () -> Objects.requireNonNull(tSupplier.get(), "This Provider is empty.");
    }

    /**
     * @implSpec Must throw {@link NullPointerException} if the value is null.
     *
     * @throws NullPointerException if the value contained within this Provider is null.
     * @return The value.
     */
    T get();

    default Provider<T> filter(Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        return () -> {
            final T t = this.get();
            if (filter.test(t)) {
                return t;
            } else {
                throw new NullPointerException("This Provider is empty.");
            }
        };
    }

    default <U> Provider<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return () -> mapper.apply(this.get());
    }

    default <U> Provider<U> flatMap(Function<? super T, ? extends Provider<? extends U>> flatMapper) {
        Objects.requireNonNull(flatMapper);
        return () -> flatMapper.apply(this.get()).get();
    }

    default Provider<T> zip(SemiGroup<T> semiGroup, Provider<? extends T> other) {
        Objects.requireNonNull(semiGroup);
        Objects.requireNonNull(other);
        return () -> semiGroup.concat(this.get(), other.get());
    }

}
