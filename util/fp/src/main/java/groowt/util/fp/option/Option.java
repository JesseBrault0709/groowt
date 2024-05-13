package groowt.util.fp.option;

import groowt.util.fp.hkt.Monoid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract sealed class Option<T> {

    private static EmptyOption<Object> emptyInstance;

    public static <T> Option<T> lift(T value) {
        return new ValueOption<>(Objects.requireNonNull(value));
    }

    public static <T> Option<T> liftNullable(@Nullable T value) {
        return value == null ? empty() : lift(value);
    }

    public static <T> Option<T> liftLazy(Supplier<? extends @NotNull T> valueSupplier) {
        return new SupplierOption<>(Objects.requireNonNull(valueSupplier));
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<T> empty() {
        if (emptyInstance == null) {
            emptyInstance = new EmptyOption<>();
        }
        return (Option<T>) emptyInstance;
    }

    private static final class EmptyOption<T> extends Option<T> {

        @Override
        public T get() {
            throw new NullPointerException("Cannot get() on EmptyOption");
        }

        @Override
        public boolean isPresent() {
            return false;
        }

    }

    private static final class ValueOption<T> extends Option<T> {

        private final T value;

        public ValueOption(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return this.value;
        }

    }

    private static final class SupplierOption<T> extends Option<T> {

        private final Supplier<T> valueSupplier;

        public SupplierOption(Supplier<? extends T> valueSupplier) {
            this.valueSupplier = valueSupplier::get;
        }

        @Override
        public T get() {
            return Objects.requireNonNull(
                    this.valueSupplier.get(),
                    "Cannot get() when the given valueSupplier returns null."
            );
        }

    }

    public abstract T get();

    public boolean isPresent() {
        return true;
    }

    public @NotNull T getOrElse(T other) {
        return this.isPresent() ? this.get() : Objects.requireNonNull(other);
    }

    public @Nullable T getOrElseNull() {
        return this.isPresent() ? this.get() : null;
    }

    public Option<T> orElseLift(T other) {
        return this.isPresent() ? this : new ValueOption<>(Objects.requireNonNull(other));
    }

    public Option<T> orElseLiftLazy(Supplier<? extends @NotNull T> lazyOther) {
        return this.isPresent() ? this : new SupplierOption<>(Objects.requireNonNull(lazyOther));
    }

    public <U> Option<U> map(Function<? super T, ? extends @NotNull U> mapper) {
        return new SupplierOption<>(() -> mapper.apply(this.get()));
    }

    public <U> Option<U> mapLazy(Function<? super T, ? extends Supplier<? extends @NotNull U>> lazyMapper) {
        return new SupplierOption<>(() -> lazyMapper.apply(this.get()).get());
    }

    public <U> Option<U> flatMap(Function<? super T, Option<? extends U>> mapper) {
        return new SupplierOption<>(() -> mapper.apply(this.get()).get());
    }

    public <U> Option<U> flatMapLazy(Function<? super T, Option<Supplier<? extends @NotNull U>>> lazyMapper) {
        return new SupplierOption<>(() -> lazyMapper.apply(this.get()).get().get());
    }

    public void ifPresent(Consumer<T> onPresent) {
        if (this.isPresent()) {
            onPresent.accept(this.get());
        }
    }

    public void ifPresentOrElse(Consumer<T> onPresent, Runnable orElse) {
        if (this.isPresent()) {
            onPresent.accept(this.get());
        } else {
            orElse.run();
        }
    }

    public <R> R fold(Function<? super T, ? extends R> onPresent, Supplier<? extends R> onEmpty) {
        if (this.isPresent()) {
            return onPresent.apply(this.get());
        } else {
            return onEmpty.get();
        }
    }

    public <R> R foldMap(Monoid<R> monoid, Function<? super T, ? extends R> onPresent) {
        if (this.isPresent()) {
            return onPresent.apply(this.get());
        } else {
            return monoid.empty();
        }
    }

    public <R> R foldFlatMap(Monoid<R> monoid, Function<? super T, Option<? extends R>> onPresent) {
        if (this.isPresent()) {
            return onPresent.apply(this.get()).get();
        } else {
            return monoid.empty();
        }
    }

}
