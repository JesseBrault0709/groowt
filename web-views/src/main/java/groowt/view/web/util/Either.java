package groowt.view.web.util;

import java.util.function.Function;

public sealed interface Either<E, T> {

    @SuppressWarnings("unchecked")
    static <E, T> Either<E, T> left(E error) {
        return (Either<E, T>) new Left<>(error);
    }

    @SuppressWarnings("unchecked")
    static <E, T> Either<E, T> right(T item) {
        return (Either<E, T>) new Right<>(item);
    }

    final class Left<E> implements Either<E, Object> {

        private final E error;

        public Left(E error) {
            this.error = error;
        }

        public E get() {
            return this.error;
        }

    }

    final class Right<T> implements Either<Object, T> {

        private final T item;

        public Right(T item) {
            this.item = item;
        }

        public T get() {
            return this.item;
        }

    }

    default boolean isLeft() {
        return this instanceof Either.Left;
    }

    default boolean isRight() {
        return this instanceof Either.Right;
    }

    @SuppressWarnings("unchecked")
    default Left<E> asLeft() {
        return (Left<E>) this;
    }

    @SuppressWarnings("unchecked")
    default Right<T> asRight() {
        return (Right<T>) this;
    }

    default E getLeft() {
        return this.asLeft().get();
    }

    default T getRight() {
        return this.asRight().get();
    }

    @SuppressWarnings("unchecked")
    default Either<E, T> mapLeft(Function<? super E, ? extends T> onLeft) {
        if (this.isLeft()) {
            return (Either<E, T>) new Right<>(onLeft.apply(this.getLeft()));
        } else {
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    default Either<E, T> flatMapLeft(Function<? super E, Either<E, ? extends T>> onLeft) {
        if (this.isLeft()) {
            final var error = this.getLeft();
            return (Either<E, T>) onLeft.apply(error);
        } else {
            return this;
        }
    }

}
