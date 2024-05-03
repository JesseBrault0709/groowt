package groowt.util.di;

import groowt.util.di.filters.FilterHandler;
import groowt.util.di.filters.IterableFilterHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static groowt.util.di.RegistryObjectFactoryUtil.orElseSupply;

public abstract class AbstractRegistryObjectFactory extends AbstractInjectingObjectFactory implements RegistryObjectFactory {

    public static abstract class AbstractBuilder<T extends DefaultRegistryObjectFactory> implements Builder<T> {

        private final Collection<FilterHandler<?, ?>> filterHandlers = new ArrayList<>();
        private final Collection<IterableFilterHandler<?, ?>> iterableFilterHandlers = new ArrayList<>();
        private final Registry registry;
        private @Nullable RegistryObjectFactory parent;

        public AbstractBuilder(Registry registry) {
            this.registry = registry;
        }

        public AbstractBuilder() {
            this.registry = new DefaultRegistry();
        }

        protected Registry getRegistry() {
            return this.registry;
        }

        protected Collection<FilterHandler<?, ?>> getFilterHandlers() {
            return this.filterHandlers;
        }

        protected Collection<IterableFilterHandler<?, ?>> getIterableFilterHandlers() {
            return this.iterableFilterHandlers;
        }

        protected @Nullable RegistryObjectFactory getParent() {
            return this.parent;
        }

        @Override
        public void configureRegistry(Consumer<? super Registry> configure) {
            configure.accept(this.registry);
        }

        public void addFilterHandler(FilterHandler<?, ?> handler) {
            this.filterHandlers.add(handler);
        }

        public void addIterableFilterHandler(IterableFilterHandler<?, ?> handler) {
            this.iterableFilterHandlers.add(handler);
        }

        public void setParent(@Nullable RegistryObjectFactory parent) {
            this.parent = parent;
        }

    }

    protected final Registry registry;
    @Nullable private final RegistryObjectFactory parent;

    public AbstractRegistryObjectFactory(Registry registry, @Nullable RegistryObjectFactory parent) {
        this.registry = registry;
        this.parent = parent;
    }

    @Override
    public void configureRegistry(Consumer<? super Registry> use) {
        use.accept(this.registry);
    }

    @Override
    public <A extends Annotation> @Nullable ScopeHandler<A> findScopeHandler(Class<A> scopeType) {
        return this.registry.getScopeHandler(scopeType);
    }

    @Override
    public <A extends Annotation> @Nullable QualifierHandler<A> findQualifierHandler(Class<A> qualifierType) {
        return this.registry.getQualifierHandler(qualifierType);
    }

    protected final <T> Optional<T> findInParent(Function<? super RegistryObjectFactory, @Nullable T> finder) {
        return this.parent != null ? Optional.ofNullable(finder.apply(this.parent)) : Optional.empty();
    }

    protected final <T> Optional<T> findInSelfOrParent(Function<? super RegistryObjectFactory, @Nullable T> finder) {
        return orElseSupply(
                finder.apply(this),
                () -> this.parent != null ? finder.apply(this.parent) : null
        );
    }

    protected final <T> T getInSelfOrParent(
            Function<? super RegistryObjectFactory, @Nullable T> finder,
            Supplier<? extends RuntimeException> exceptionSupplier
    ) {
        return orElseSupply(
                finder.apply(this),
                () -> this.parent != null ? finder.apply(this.parent) : null
        ).orElseThrow(exceptionSupplier);
    }

}
