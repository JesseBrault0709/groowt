package groowt.util.di;

import groowt.util.di.filters.Filter;
import groowt.util.di.filters.IterableFilter;
import jakarta.inject.Qualifier;
import jakarta.inject.Scope;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class RegistryObjectFactoryUtil {

    private RegistryObjectFactoryUtil() {}

    public static List<Annotation> getQualifierAnnotations(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                .toList();
    }

    public static List<Annotation> getFilterAnnotations(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(a -> a.annotationType().isAnnotationPresent(Filter.class))
                .toList();
    }

    public static List<Annotation> getIterableFilterAnnotations(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(a -> a.annotationType().isAnnotationPresent(IterableFilter.class))
                .toList();
    }

    public static <T> Optional<T> orElseSupply(T first, Supplier<T> onNullFirst) {
        return first != null ? Optional.of(first) : Optional.ofNullable(onNullFirst.get());
    }

    public static void checkIsValidFilter(Class<? extends Annotation> annotationClass) {
        if (!annotationClass.isAnnotationPresent(Filter.class)) {
            throw new IllegalArgumentException(
                    "The given filter annotation " + annotationClass.getName() + " is itself not annotated with @Filter"
            );
        }
    }

    public static void checkIsValidIterableFilter(Class<? extends Annotation> annotationClass) {
        if (!annotationClass.isAnnotationPresent(IterableFilter.class)) {
            throw new IllegalArgumentException(
                    "The given iterable filter annotation " + annotationClass.getName() + " is itself not annotated with @IterableFilter"
            );
        }
    }

    public static @Nullable Annotation getScopeAnnotation(Class<?> clazz) {
        final List<Annotation> scopeAnnotations = Arrays.stream(clazz.getAnnotations())
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Scope.class))
                .toList();
        if (scopeAnnotations.size() > 1) {
            throw new RuntimeException(clazz.getName() + " has too many annotations that are themselves annotated with @Scope");
        }
        return scopeAnnotations.size() == 1 ? scopeAnnotations.getFirst() : null;
    }

}
