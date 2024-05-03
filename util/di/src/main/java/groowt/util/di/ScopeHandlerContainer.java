package groowt.util.di;

import jakarta.inject.Scope;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public interface ScopeHandlerContainer {

    static void checkIsValidScope(Class<? extends Annotation> scope) {
        if (!scope.isAnnotationPresent(Scope.class)) {
            throw new IllegalArgumentException(
                    "The given scope annotation " + scope + " is itself not annotated with @Scope"
            );
        }
    }

    <A extends Annotation> @Nullable ScopeHandler<A> getScopeHandler(Class<A> scopeType);

}
