package groowt.util.di.filters;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.BiPredicate;

final class SimpleIterableFilterHandler<A extends Annotation, E> implements IterableFilterHandler<A, E> {

    private final Class<A> annotationClass;
    private final BiPredicate<A, E> elementPredicate;

    public SimpleIterableFilterHandler(Class<A> annotationClass, BiPredicate<A, E> elementPredicate) {
        this.annotationClass = annotationClass;
        this.elementPredicate = elementPredicate;
    }

    @Override
    public boolean check(A annotation, Iterable<E> iterable) {
        for (final var e : Objects.requireNonNull(iterable)) {
            if (!this.elementPredicate.test(annotation, e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Class<A> getAnnotationClass() {
        return this.annotationClass;
    }

}
