package groowt.view.web.util;

public sealed interface Either<E, T> {

    @SuppressWarnings("unchecked")
    static <E, T> Either<E, T> left(E error) {
        return (Either<E, T>) new Left<>((Class<E>) error.getClass(), error);
    }

    @SuppressWarnings("unchecked")
    static <E, T> Either<E, T> right(T item) {
        return (Either<E, T>) new Right<>((Class<T>) item.getClass(), item);
    }

    record Left<E>(Class<E> errorClass, E error) implements Either<E, Object> {

        public E get() {
            return this.errorClass.cast(this.error);
        }
    }

    record Right<T>(Class<T> itemClass, T item) implements Either<Object, T> {

        public T get() {
            return this.itemClass.cast(this.item);
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

}
