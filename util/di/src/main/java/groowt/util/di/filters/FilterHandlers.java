package groowt.util.di.filters;

import java.lang.annotation.*;
import java.util.function.BiPredicate;

import static groowt.util.di.filters.FilterUtil.isAssignableToAnyOf;

public final class FilterHandlers {

    private FilterHandlers() {}

    public static <A extends Annotation, T> FilterHandler<A, T> of(
            Class<A> annotationClass,
            Class<T> argClass,
            BiPredicate<A, T> predicate
    ) {
        return new SimpleFilterHandler<>(predicate, annotationClass, argClass);
    }

    @Filter
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface AllowTypes {
        Class<?>[] value();
    }

    public static <T> FilterHandler<AllowTypes, T> getAllowsTypesFilterHandler(Class<T> targetType) {
        return of(
                AllowTypes.class,
                targetType,
                (annotation, target) -> isAssignableToAnyOf(target.getClass(), annotation.value())
        );
    }

}
