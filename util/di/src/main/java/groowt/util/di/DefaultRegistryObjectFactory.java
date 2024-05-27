package groowt.util.di;

import groowt.util.di.filters.FilterHandler;
import groowt.util.di.filters.IterableFilterHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static groowt.util.di.RegistryObjectFactoryUtil.*;

public class DefaultRegistryObjectFactory extends AbstractRegistryObjectFactory {

    public static final class Builder
            extends AbstractRegistryObjectFactory.AbstractBuilder<DefaultRegistryObjectFactory> {

        /**
         * Creates a {@code Builder} initialized with a {@link DefaultRegistry}, which is in-turn configured with a
         * {@link NamedRegistryExtension} and a {@link SingletonScopeHandler}.
         *
         * @return the builder
         */
        public static Builder withDefaults() {
            final var b = new Builder();

            b.configureRegistry(r -> {
                r.addExtension(new DefaultNamedRegistryExtension());
                r.addExtension(new SingletonRegistryExtension(r));
            });

            return b;
        }

        /**
         * @return a blank builder with a {@link Registry} from the given {@link Supplier}.
         */
        public static Builder withRegistry(Supplier<? extends Registry> registrySupplier) {
            return new Builder(registrySupplier.get());
        }

        /**
         * @return a blank builder which will use {@link DefaultRegistry}.
         */
        public static Builder blank() {
            return new Builder();
        }

        private Builder(Registry registry) {
            super(registry);
        }

        private Builder() {
            super();
        }

        @Override
        public DefaultRegistryObjectFactory build() {
            return new DefaultRegistryObjectFactory(
                    this.getRegistry(),
                    this.getParent(),
                    this.getFilterHandlers(),
                    this.getIterableFilterHandlers()
            );
        }

    }

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private final Collection<FilterHandler<?, ?>> filterHandlers;
    private final Collection<IterableFilterHandler<?, ?>> iterableFilterHandlers;

    protected DefaultRegistryObjectFactory(
            Registry registry,
            @Nullable RegistryObjectFactory parent,
            Collection<? extends FilterHandler<?, ?>> filterHandlers,
            Collection<? extends IterableFilterHandler<?, ?>> iterableFilterHandlers
    ) {
        super(registry, parent);
        this.filterHandlers = new ArrayList<>(filterHandlers);
        this.filterHandlers.forEach(handler -> checkIsValidFilter(handler.getAnnotationClass()));

        this.iterableFilterHandlers = new ArrayList<>(iterableFilterHandlers);
        this.iterableFilterHandlers.forEach(handler -> checkIsValidIterableFilter(handler.getAnnotationClass()));
    }

    /**
     * Checks if the given parameter has any qualifier annotations; if it does,
     * it delegates finding the desired object to the registered {@link QualifierHandler}.
     *
     * @param parameter the parameter
     * @return the object returned from the {@code QualifierHandler}, or {@code null} if no qualifier
     * is present or the {@code QualifierHandler} itself returns {@code null}.
     *
     * @throws RuntimeException if no {@code QualifierHandler} is registered for a qualifier annotation present on the
     * given parameter, or if the handler itself throws an exception.
     */
    @SuppressWarnings("unchecked")
    protected final @Nullable Object tryQualifiers(Parameter parameter) {
        final Class<?> paramType = parameter.getType();
        final List<Annotation> qualifiers = RegistryObjectFactoryUtil.getQualifierAnnotations(
                parameter.getAnnotations()
        );
        if (qualifiers.size() > 1) {
            throw new RuntimeException("Parameter " + parameter + " cannot have more than one Qualifier annotation.");
        } else if (qualifiers.size() == 1) {
            final Annotation qualifier = qualifiers.getFirst();
            @SuppressWarnings("rawtypes")
            final QualifierHandler handler = this.getInSelfOrParent(
                    f -> f.findQualifierHandler(qualifier.annotationType()),
                    () -> new RuntimeException("There is no configured QualifierHandler for "
                            + qualifier.annotationType().getName()
                    )
            );
            final Binding<?> binding = handler.handle(qualifier, paramType);
            if (binding != null) {
                return this.handleBinding(binding, EMPTY_OBJECT_ARRAY);
            }
        }
        // no Qualifier or the QualifierHandler didn't return a Binding
        return null;
    }

    /**
     * Checks the {@code resolvedArg} against all filters present on the given parameter.
     *
     * @param parameter the parameter
     * @param resolvedArg the resolved argument
     *
     * @throws RuntimeException if the {@link FilterHandler} itself throws an exception.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final void checkFilters(Parameter parameter, Object resolvedArg) {
        final Annotation[] allAnnotations = parameter.getAnnotations();
        final Collection<Annotation> filterAnnotations = getFilterAnnotations(allAnnotations);
        if (!filterAnnotations.isEmpty()) {
            final Collection<FilterHandler<?, ?>> filtersForParamType = this.filterHandlers.stream()
                    .filter(filterHandler ->
                            filterHandler.getArgumentClass().isAssignableFrom(parameter.getType())
                    )
                    .toList();
            for (final Annotation filterAnnotation : filterAnnotations) {
                for (final FilterHandler<?, ?> filterHandler : filtersForParamType) {
                    if (filterAnnotation.annotationType().equals(filterHandler.getAnnotationClass())) {
                        // hopefully we've checked everything
                        ((FilterHandler<Annotation, Object>) filterHandler).check(filterAnnotation, resolvedArg);
                    }
                }
            }
        }
        final Collection<Annotation> iterableFilterAnnotations = getIterableFilterAnnotations(allAnnotations);
        if (!iterableFilterAnnotations.isEmpty() && resolvedArg instanceof Iterable iterable) {
            for (final var annotation : iterableFilterAnnotations) {
                this.iterableFilterHandlers.stream()
                        .filter(handler -> handler.getAnnotationClass().equals(annotation.annotationType()))
                        .forEach(handler -> {
                            ((IterableFilterHandler<Annotation, Object>) handler).check(annotation, iterable);
                        });
            }
        }
    }

    protected final Object resolveInjectedArg(CreateContext context, Parameter parameter) {
        final Object qualifierProvidedArg = this.tryQualifiers(parameter);
        if (qualifierProvidedArg != null) {
            this.checkFilters(parameter, qualifierProvidedArg);
            context.getAllResolved().add(new Resolved(parameter.getType(), qualifierProvidedArg));
            return qualifierProvidedArg;
        } else {
            final Object created = this.get(context, parameter.getType());
            this.checkFilters(parameter, created);
            context.getAllResolved().add(new Resolved(parameter.getType(), created));
            return created;
        }
    }

    protected final void resolveInjectedArgs(CreateContext context, Object[] dest, Parameter[] params) {
        for (int i = 0; i < params.length; i++) {
            dest[i] = this.resolveInjectedArg(context, params[i]);
        }
    }

    protected final void resolveGivenArgs(Object[] dest, Parameter[] params, Object[] givenArgs, int startIndex) {
        for (int i = startIndex; i < dest.length; i++) {
            final int resolveIndex = i - startIndex;
            final Object arg = givenArgs[resolveIndex];
            this.checkFilters(params[resolveIndex], arg);
            dest[i] = arg;
        }
    }

    // TODO: when there is a null arg, we lose the type. Therefore this algorithm breaks. Fix this.
    @Override
    protected Object[] createArgs(CreateContext context, Constructor<?> constructor, Object[] givenArgs) {
        final Class<?>[] paramTypes = constructor.getParameterTypes();

        // check no arg
        if (paramTypes.length == 0 && givenArgs.length == 0) {
            // no args given, none needed, so return empty array
            return EMPTY_OBJECT_ARRAY;
        } else if (paramTypes.length == 0) { // implicit that givenArgs.length != 0
            // zero expected, but got given args
            throw new RuntimeException(
                    "Expected zero args for constructor " + constructor + "  but received " + Arrays.toString(givenArgs)
            );
        } else if (givenArgs.length > paramTypes.length) {
            // expected is more than zero, but received too many given
            throw new RuntimeException(
                    "Too many args given for constructor " + constructor + "; received " + Arrays.toString(givenArgs)
            );
        }

        final Parameter[] allParams = constructor.getParameters();
        final Object[] resolvedArgs = new Object[allParams.length];

        if (givenArgs.length == 0) {
            // if no given args, then they are all injected
            this.resolveInjectedArgs(context, resolvedArgs, allParams);
        } else if (givenArgs.length == paramTypes.length) {
            // all are given
            this.resolveGivenArgs(resolvedArgs, allParams, givenArgs, 0);
        } else {
            // some are injected, some are given
            // everything before (non-inclusive) is injected
            // everything after (inclusive) is given
            // ex: 1 inject, 1 given -> 2 (allParams) - 1 = 1
            // ex: 0 inject, 1 given -> 1 - 1 = 0
            final int firstGivenIndex = allParams.length - givenArgs.length;

            final Parameter[] injectedParams = new Parameter[firstGivenIndex];
            final Parameter[] givenParams = new Parameter[allParams.length - firstGivenIndex];

            System.arraycopy(allParams, 0, injectedParams, 0, injectedParams.length);
            System.arraycopy(
                    allParams, firstGivenIndex, givenParams, 0, allParams.length - firstGivenIndex
            );

            this.resolveInjectedArgs(context, resolvedArgs, injectedParams);
            this.resolveGivenArgs(resolvedArgs, givenParams, givenArgs, firstGivenIndex);
        }

        return resolvedArgs;
    }

    private <T> T handleBinding(Binding<T> binding, Object[] constructorArgs) {
        return this.handleBinding(binding, null, constructorArgs);
    }

    @SuppressWarnings("unchecked")
    private <T> T handleBinding(Binding<T> binding, @Nullable CreateContext context, Object[] constructorArgs) {
        return switch (binding) {
            case ClassBinding<T>(Class<T> ignored, Class<? extends T> to) -> {
                final Annotation scopeAnnotation = getScopeAnnotation(to);
                if (scopeAnnotation != null) {
                    final Class<? extends Annotation> scopeClass = scopeAnnotation.annotationType();
                    @SuppressWarnings("rawtypes")
                    final ScopeHandler scopeHandler = this.getInSelfOrParent(
                            f -> f.findScopeHandler(scopeClass),
                            () -> new RuntimeException(
                                    "There is no configured ScopeHandler for " + scopeClass.getName()
                            )
                    );
                    final Binding<T> scopedBinding = scopeHandler.onScopedDependencyRequest(
                            scopeAnnotation, to, this
                    );
                    yield this.handleBinding(scopedBinding, constructorArgs);
                } else {
                    if (context != null) {
                        yield this.createInstance(context, to, constructorArgs);
                    } else {
                        yield this.createInstance(to, constructorArgs);
                    }
                }
            }
            case ProviderBinding<T> providerBinding -> providerBinding.provider().get();
            case SingletonBinding<T> singletonBinding -> singletonBinding.to();
            case LazySingletonBinding<T> lazySingletonBinding -> lazySingletonBinding.singletonSupplier().get();
        };
    }

    protected final <T> @Nullable Binding<T> searchRegistry(Class<T> from) {
        return this.registry.getBinding(from);
    }

    protected @Nullable <T> T tryParent(Class<T> clazz, Object[] constructorArgs) {
        return this.findInParent(f -> f.getOrNull(clazz, constructorArgs)).orElse(null);
    }

    @Override
    protected Object getSetterInjectArg(CreateContext context, Class<?> targetType, Method setter, Parameter toInject) {
        return this.resolveInjectedArg(context, toInject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T get(Class<T> clazz, Object... constructorArgs) {
        final Binding<T> binding = this.searchRegistry(clazz);
        if (binding != null) {
            return this.handleBinding(binding, constructorArgs);
        }
        final T parentResult = this.tryParent(clazz, constructorArgs);
        if (parentResult != null) {
            return parentResult;
        } else {
            throw new RuntimeException(
                    "No bindings for " + clazz + " with args " + Arrays.toString(constructorArgs) + "."
            );
        }
    }

    protected <T> T get(CreateContext context, Class<T> type) {
        final Binding<T> binding = this.searchRegistry(type);
        if (binding != null) {
            return this.handleBinding(binding, context, EMPTY_OBJECT_ARRAY);
        }
        final T parentResult = this.tryParent(type, EMPTY_OBJECT_ARRAY);
        if (parentResult != null) {
            return parentResult;
        } else {
            throw new RuntimeException(
                    "No bindings for " + type + " with args " + Arrays.toString(EMPTY_OBJECT_ARRAY) + "."
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getOrDefault(Class<T> clazz, T defaultValue, Object... constructorArgs) {
        final Binding<T> binding = this.searchRegistry(clazz);
        if (binding != null) {
            return this.handleBinding(binding, constructorArgs);
        }
        final T parentResult = this.tryParent(clazz, constructorArgs);
        return parentResult != null ? parentResult : defaultValue;
    }

}
