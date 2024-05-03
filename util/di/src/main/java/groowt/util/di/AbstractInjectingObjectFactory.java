package groowt.util.di;

import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;

import static groowt.util.di.ObjectFactoryUtil.toTypes;

// TODO: maybe inject fields
public abstract class AbstractInjectingObjectFactory implements ObjectFactory {

    protected record CachedInjectConstructor<T>(Class<T> clazz, Constructor<T> constructor) {}

    protected record CachedNonInjectConstructor<T>(
            Class<T> clazz,
            Constructor<T> constructor,
            Class<?>[] paramTypes
    ) {}

    private final Map<Class<?>, Constructor<?>[]> cachedAllConstructors = new HashMap<>();
    private final Collection<CachedInjectConstructor<?>> cachedInjectConstructors = new ArrayList<>();
    private final Collection<CachedNonInjectConstructor<?>> cachedNonInjectConstructors = new ArrayList<>();
    private final Map<Class<?>, Collection<Method>> cachedSetters = new HashMap<>();
    private final Map<Method, Parameter> cachedSetterParameters = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <T> @Nullable Constructor<T> findCachedInjectConstructor(Class<T> clazz) {
        for (final CachedInjectConstructor<?> cachedConstructor : this.cachedInjectConstructors) {
            if (clazz.equals(cachedConstructor.clazz())) {
                return (Constructor<T>) cachedConstructor.constructor();
            }
        }
        return null;
    }

    /**
     * @implNote If overridden, please cache any found inject constructors using {@link #putCachedInjectConstructor}.
     *
     * @param clazz the {@link Class} in which to search for an <code>{@literal @}Inject</code> annotated constructor.
     * @return the inject constructor, or {@code null} if none found.
     * @param <T> the type of the class
     */
    @SuppressWarnings("unchecked")
    protected <T> @Nullable Constructor<T> findInjectConstructor(Class<T> clazz) {
        final Constructor<T> cachedInjectConstructor  = this.findCachedInjectConstructor(clazz);
        if (cachedInjectConstructor != null) {
            return cachedInjectConstructor;
        }

        final Constructor<?>[] constructors = this.cachedAllConstructors.computeIfAbsent(clazz, Class::getConstructors);

        final List<Constructor<?>> injectConstructors = Arrays.stream(constructors)
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .toList();

        if (injectConstructors.size() > 1) {
            // one day maybe support multiple inject constructors
            throw new UnsupportedOperationException("Cannot have more than one @Inject constructor in class: " + clazz);
        } else if (injectConstructors.size() == 1) {
            final Constructor<T> injectConstructor = (Constructor<T>) injectConstructors.getFirst();
            this.putCachedInjectConstructor(new CachedInjectConstructor<>(clazz, injectConstructor));
            return injectConstructor;
        } else {
            return null;
        }
    }

    protected final void putCachedInjectConstructor(CachedInjectConstructor<?> cached) {
        this.cachedInjectConstructors.add(cached);
    }

    @SuppressWarnings("unchecked")
    private <T> @Nullable Constructor<T> findCachedNonInjectConstructor(Class<T> clazz, Class<?>[] paramTypes) {
        for (final CachedNonInjectConstructor<?> cachedConstructor : this.cachedNonInjectConstructors) {
            if (clazz.equals(cachedConstructor.clazz()) && Arrays.equals(cachedConstructor.paramTypes(), paramTypes)) {
                return (Constructor<T>) cachedConstructor.constructor();
            }
        }
        return null;
    }

    /**
     * @implNote If overridden, please cache any found non-inject constructors using {@link #putCachedNonInjectConstructor}.
     *
     * @param clazz the {@link Class} in which to search for a constructor which does not have an <code>{@literal @}Inject</code>
     *              annotation
     * @param constructorArgs the given constructor args
     * @return the found non-inject constructor appropriate for the given constructor args, or {@code null} if no
     * such constructor exists
     * @param <T> the type
     */
    @SuppressWarnings("unchecked")
    protected <T> @Nullable Constructor<T> findNonInjectConstructor(Class<T> clazz, Object[] constructorArgs) {
        final Class<?>[] types = toTypes(constructorArgs);
        final Constructor<T> cachedConstructor = this.findCachedNonInjectConstructor(clazz, types);
        if (cachedConstructor != null) {
            return cachedConstructor;
        }

        final Constructor<?>[] constructors = this.cachedAllConstructors.computeIfAbsent(clazz, Class::getConstructors);
        for (Constructor<?> constructor : constructors) {
            if (Arrays.equals(constructor.getParameterTypes(), types)) {
                final Constructor<T> found = (Constructor<T>) constructor;
                this.putCachedNonInjectConstructor(new CachedNonInjectConstructor<>(clazz, found, types));
                return found;
            }
        }
        return null;
    }

    protected final void putCachedNonInjectConstructor(CachedNonInjectConstructor<?> cached) {
        this.cachedNonInjectConstructors.add(cached);
    }

    /**
     * @implNote Please call {@code super.findConstructor()} first, and then implement custom
     * constructor finding logic. If the custom logic finds a constructor, please cache it
     * using either {@link #putCachedNonInjectConstructor} or {@link #putCachedInjectConstructor}.
     */
    protected <T> Constructor<T> findConstructor(Class<T> clazz, Object[] args) {
        final Constructor<T> injectConstructor = this.findInjectConstructor(clazz);
        if (injectConstructor != null) {
            return injectConstructor;
        }
        final Constructor<T> nonInjectConstructor = this.findNonInjectConstructor(clazz, args);
        if (nonInjectConstructor != null) {
            return nonInjectConstructor;
        }
        throw new RuntimeException("Could not find an appropriate constructor for " + clazz.getName()
                + " with args " + Arrays.toString(toTypes(args))
        );
    }

    protected Collection<Method> getAllInjectSetters(Class<?> clazz) {
        final Method[] allMethods = clazz.getMethods();
        final Collection<Method> injectSetters = new ArrayList<>();
        for (final var method : allMethods) {
            if (
                    method.isAnnotationPresent(Inject.class)
                            && method.getName().startsWith("set")
                            && !Modifier.isStatic(method.getModifiers())
                            && method.getParameterCount() == 1
            ) {
                injectSetters.add(method);
            }
        }
        return injectSetters;
    }

    protected Collection<Method> getCachedSettersFor(Object target) {
        return this.cachedSetters.computeIfAbsent(target.getClass(), this::getAllInjectSetters);
    }

    protected Parameter getCachedInjectParameter(Method setter) {
        return this.cachedSetterParameters.computeIfAbsent(setter, s -> {
            if (s.getParameterCount() != 1) {
                throw new IllegalArgumentException("Setter " + s.getName() + " has a parameter count other than one (1)!");
            }
            return s.getParameters()[0];
        });
    }

    protected void injectSetter(Object target, Method setter) {
        try {
            setter.invoke(target, this.getSetterInjectArg(target.getClass(), setter, this.getCachedInjectParameter(setter)));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void injectSetters(Object target) {
        this.getCachedSettersFor(target).forEach(setter -> this.injectSetter(target, setter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T createInstance(Class<T> clazz, Object... constructorArgs) {
        final Constructor<T> constructor = this.findConstructor(clazz, constructorArgs);
        final Object[] allArgs = this.createArgs(constructor, constructorArgs);
        try {
            final T instance = constructor.newInstance(allArgs);
            this.injectSetters(instance);
            return instance;
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e); // In the future, we might have an option to ignore exceptions
        }
    }

    protected abstract Object[] createArgs(Constructor<?> constructor, Object[] constructorArgs);

    protected abstract Object getSetterInjectArg(Class<?> targetType, Method setter, Parameter toInject);

}
