package groowt.util.di;

import java.lang.annotation.Annotation;

public interface ScopeHandler<A extends Annotation> {
    <T> Binding<T> onScopedDependencyRequest(A annotation, Class<T> dependencyClass, RegistryObjectFactory objectFactory);
    void reset();
}
