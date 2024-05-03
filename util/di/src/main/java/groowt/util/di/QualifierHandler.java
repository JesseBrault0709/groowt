package groowt.util.di;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface QualifierHandler<A extends Annotation> {
    <T> @Nullable Binding<T> handle(A annotation, Class<T> dependencyClass);
}
