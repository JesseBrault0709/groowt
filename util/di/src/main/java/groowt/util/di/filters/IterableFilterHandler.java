package groowt.util.di.filters;

import java.lang.annotation.Annotation;

public interface IterableFilterHandler<A extends Annotation, E> {
    boolean check(A annotation, Iterable<E> iterable);
    Class<A> getAnnotationClass();
}
