package groowt.util.di;

import jakarta.inject.Singleton;

import static groowt.util.di.BindingUtil.toSingleton;

public final class SingletonScopeHandler implements ScopeHandler<Singleton> {

    private final Registry owner;

    public SingletonScopeHandler(Registry owner) {
        this.owner = owner;
    }

    @Override
    public <T> Binding<T> onScopedDependencyRequest(
            Singleton annotation,
            Class<T> dependencyClass,
            RegistryObjectFactory objectFactory
    ) {
        final Binding<T> potentialBinding = this.owner.getBinding(dependencyClass);
        if (potentialBinding != null) {
            return potentialBinding;
        } else {
            this.owner.bind(dependencyClass, toSingleton(objectFactory.createInstance(dependencyClass)));
            return this.owner.getBinding(dependencyClass);
        }
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Cannot reset the Singleton scope!");
    }

}
