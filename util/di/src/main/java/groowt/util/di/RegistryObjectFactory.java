package groowt.util.di;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groowt.util.di.filters.FilterHandler;
import groowt.util.di.filters.IterableFilterHandler;
import jakarta.inject.Provider;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

/**
 * A {@link RegistryObjectFactory} is an {@link ObjectFactory} that offers the ability
 * to provide desired objects based on an instance of
 * {@link Registry} to determine how to provide those objects.
 */
public interface RegistryObjectFactory extends ObjectFactory {

    interface Builder<T extends RegistryObjectFactory> {

        void configureRegistry(Consumer<? super Registry> configure);

        default void configureRegistry(@DelegatesTo(Registry.class) Closure<?> configureClosure) {
            this.configureRegistry(registry -> {
                configureClosure.setDelegate(registry);
                configureClosure.call();
            });
        }

        void addFilterHandler(FilterHandler<?, ?> handler);

        void addIterableFilterHandler(IterableFilterHandler<?, ?> handler);

        T build();

    }

    Registry getRegistry();

    default void configureRegistry(Consumer<? super Registry> use) {
        use.accept(this.getRegistry());
    }

    default void configureRegistry(
            @DelegatesTo(value = Registry.class)
            Closure<?> configureClosure
    ) {
        final Registry registry = this.getRegistry();
        configureClosure.setDelegate(registry);
        configureClosure.call();
    }

    <A extends Annotation> @Nullable ScopeHandler<A> findScopeHandler(Class<A> scopeType);

    <A extends Annotation> @Nullable QualifierHandler<A> findQualifierHandler(Class<A> qualifierType);

    /**
     * Get an object with the desired type. How it is retrieved/created
     * depends upon the {@link Binding} present in this {@link RegistryObjectFactory}'s held
     * instances of {@link Registry}. The type of the {@link Binding} determines
     * how the object is fetched:
     *
     * <ul>
     *     <li>{@link ClassBinding}: A new instance of the object is created using the given {@code constructorArgs}.</li>
     *     <li>{@link ProviderBinding}: An instance of the object is fetched from the bound {@link Provider}.
     *     Whether the instance is new or not depends on the {@link Provider}.</li>
     *     <li>{@link SingletonBinding}: The bound singleton object is returned.</li>
     * </ul>
     *
     * @implNote If {@code constructorArgs} are provided
     * and the {@link Binding} for the desired type is not a
     * {@link ClassBinding}, the implementation should
     * either throw an exception or log a warning at the least.
     *
     * @param clazz the {@link Class} of the desired type
     * @param constructorArgs As in {@link #createInstance(Class, Object...)},
     *                        the arguments which will be used to create the desired object
     *                        if the {@link Binding} is a {@link ClassBinding}.
     * @return an object of the desired type
     * @param <T> the desired type
     *
     * @throws RuntimeException if there is no registered {@link Binding} or there is a problem
     * fetching or constructing the object.
     */
    <T> T get(Class<T> clazz, Object... constructorArgs);

    /**
     * Similarly to {@link #get(Class, Object...)}, fetches an object
     * of the desired type, but does not throw if there is no registered {@link Binding}
     * in any of the held instances of {@link Registry},
     * and instead returns the given {@code defaultValue}.
     *
     * @param clazz the {@link Class} of the desired type
     * @param defaultValue the defaultValue to return
     * @param constructorArgs see {@link #get(Class, Object...)}
     * @return an object of the desired type
     * @param <T> the desired type
     *
     * @throws RuntimeException if there <em>is</em> a registered {@link Binding} and there is a problem
     * fetching or constructing the object.
     *
     * @see #get(Class, Object...)
     */
    <T> T getOrDefault(Class<T> clazz, T defaultValue, Object... constructorArgs);

    /**
     * Similar to {@link #getOrDefault(Class, Object, Object...)}, except that
     * it returns null by default if there is no registered {@link Binding}.
     *
     * @param clazz the {@link Class} of the desired type
     * @param constructorArgs see {@link RegistryObjectFactory#get(Class, Object...)}
     * @return an object of the desired type
     * @param <T> the desired type
     *
     * @see RegistryObjectFactory#get(Class, Object...)
     * @see RegistryObjectFactory#getOrDefault(Class, Object, Object...)
     *
     * @throws RuntimeException if there <em>is</em> a registered {@code Binding} and there
     * is a problem fetching or constructing the object.
     */
    default <T> @Nullable T getOrNull(Class<T> clazz, Object... constructorArgs) {
        return this.getOrDefault(clazz, null, constructorArgs);
    }

}
