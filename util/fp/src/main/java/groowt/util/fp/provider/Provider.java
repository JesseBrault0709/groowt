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
public interface Provider<T> {

    Class<T> getType();

    /**
     * @implSpec Must throw {@link NullPointerException} if the value is null.
     *
     * @throws NullPointerException if the value contained within this Provider is null.
     * @return The value.
     */
    T get();

    default T get(Supplier<RuntimeException> onEmpty) {
        try {
            return this.get();
        } catch (NullPointerException nullPointerException) {
            final RuntimeException onEmptyException = onEmpty.get();
            onEmptyException.initCause(nullPointerException);
            throw onEmptyException;
        }
    }

    default Provider<T> filter(Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        return new DefaultProvider<>(this.getType(), () -> {
            final T t = this.get();
            if (filter.test(t)) {
                return t;
            } else {
                throw new NullPointerException("This Provider is empty (did not pass filter).");
            }
        });
    }

    default <U> Provider<U> map(Class<U> targetType, Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return new DefaultProvider<>(targetType, () -> mapper.apply(this.get()));
    }

    default <U> Provider<U> flatMap(
            Class<U> targetType,
            Function<? super T, ? extends Provider<? extends U>> flatMapper
    ) {
        Objects.requireNonNull(flatMapper);
        return new DefaultProvider<>(targetType, () -> flatMapper.apply(this.get()).get());
    }

    default Provider<T> zip(
            SemiGroup<T> semiGroup,
            Provider<? extends T> other
    ) {
        Objects.requireNonNull(semiGroup);
        Objects.requireNonNull(other);
        return new DefaultProvider<>(this.getType(), () -> semiGroup.concat(this.get(), other.get()));
    }

}
