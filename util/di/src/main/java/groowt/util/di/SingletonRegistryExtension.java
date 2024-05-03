package groowt.util.di;

import jakarta.inject.Singleton;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public class SingletonRegistryExtension implements RegistryExtension, ScopeHandlerContainer {

    private final SingletonScopeHandler handler;

    public SingletonRegistryExtension(Registry owner) {
        this.handler = new SingletonScopeHandler(owner);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <A extends Annotation> ScopeHandler<A> getScopeHandler(Class<A> scopeType) {
        return Singleton.class.isAssignableFrom(scopeType) ? (ScopeHandler<A>) this.handler : null;
    }

}
