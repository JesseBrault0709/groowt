package groowt.util.di.filters;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.BiPredicate;

public interface FilterHandler<A extends Annotation, T> {

    boolean check(A annotation, T arg);
    Class<A> getAnnotationClass();
    Class<T> getArgumentClass();

    default FilterHandler<A, T> and(BiPredicate<A, T> and) {
        Objects.requireNonNull(and);
        return new SimpleFilterHandler<>(
                (a, t) -> this.check(a, t) && and.test(a, t),
                this.getAnnotationClass(),
                this.getArgumentClass()
        );
    }

    default FilterHandler<A, T> or(BiPredicate<A, T> or) {
        Objects.requireNonNull(or);
        return new SimpleFilterHandler<>(
                (a, t) -> this.check(a, t) || or.test(a, t),
                this.getAnnotationClass(),
                this.getArgumentClass()
        );
    }

    default FilterHandler<A, T> negate() {
        return new SimpleFilterHandler<>(
                (a, t) -> !this.check(a, t),
                this.getAnnotationClass(),
                this.getArgumentClass()
        );
    }

}
