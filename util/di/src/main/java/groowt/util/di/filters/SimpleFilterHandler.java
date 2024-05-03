package groowt.util.di.filters;

import java.lang.annotation.Annotation;
import java.util.function.BiPredicate;

final class SimpleFilterHandler<A extends Annotation, T> implements FilterHandler<A, T> {

    private final BiPredicate<A, T> predicate;
    private final Class<A> annotationClass;
    private final Class<T> argClass;

    public SimpleFilterHandler(BiPredicate<A, T> predicate, Class<A> annotationClass, Class<T> argClass) {
        this.predicate = predicate;
        this.annotationClass = annotationClass;
        this.argClass = argClass;
    }

    @Override
    public boolean check(A annotation, T arg) {
        return this.predicate.test(annotation, arg);
    }

    @Override
    public Class<A> getAnnotationClass() {
        return this.annotationClass;
    }

    @Override
    public Class<T> getArgumentClass() {
        return this.argClass;
    }

}
