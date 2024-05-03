package groowt.util.di.filters;

import java.lang.annotation.*;
import java.util.function.BiPredicate;

import static groowt.util.di.filters.FilterUtil.isAssignableToAnyOf;

public final class IterableFilterHandlers {

    private IterableFilterHandlers() {}

    public static <A extends Annotation, E> IterableFilterHandler<A, E> of(
            Class<A> filterType,
            BiPredicate<A, E> elementPredicate
    ) {
        return new SimpleIterableFilterHandler<>(filterType, elementPredicate);
    }

    @IterableFilter
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface IterableElementTypes {
        Class<?>[] value();
    }

    public static IterableFilterHandler<IterableElementTypes, Object> getIterableElementTypesFilterHandler() {
        return of(
                IterableElementTypes.class,
                (annotation, element) -> isAssignableToAnyOf(element.getClass(), annotation.value())
        );
    }

}
