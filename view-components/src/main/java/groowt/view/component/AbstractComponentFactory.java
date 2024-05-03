package groowt.view.component;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaMethod;
import groovy.lang.MissingMethodException;

import java.util.*;

public abstract class AbstractComponentFactory<T extends ViewComponent> extends GroovyObjectSupport
        implements ComponentFactory<T> {

    private static final String DO_CREATE = "doCreate";
    private static final Class<?>[] EMPTY_CLASSES = new Class[0];

    private static Object[] flatten(Object... args) {
        if (args.length == 0) {
            return args;
        } else {
            final List<Object> result = new ArrayList<>(args.length);
            for (final var arg : args) {
                if (arg instanceof Object[] arr) {
                    result.addAll(Arrays.asList(arr));
                } else {
                    result.add(arg);
                }
            }
            return result.toArray(Object[]::new);
        }
    }

    private static Class<?>[] asTypes(Object[] args) {
        if (args.length == 0) {
            return EMPTY_CLASSES;
        }
        final Class<?>[] result = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].getClass();
        }
        return result;
    }

    private final Map<Class<?>[], MetaMethod> cache = new HashMap<>();

    private MetaMethod findDoCreateMethod(Object[] allArgs) {
        return this.cache.computeIfAbsent(asTypes(allArgs), types ->
                this.getMetaClass().getMetaMethod(DO_CREATE, types)
        );
    }

    @SuppressWarnings("unchecked")
    private T findAndDoCreate(ComponentContext componentContext, Object[] args) {
        final Object[] contextsAndArgs = flatten(componentContext, args);
        final MetaMethod contextsAndArgsMethod = this.findDoCreateMethod(contextsAndArgs);
        if (contextsAndArgsMethod != null) {
            return (T) contextsAndArgsMethod.invoke(this, contextsAndArgs);
        }

        final Object[] contextOnly = new Object[] { componentContext };
        final MetaMethod contextOnlyMethod = this.findDoCreateMethod(contextOnly);
        if (contextOnlyMethod != null) {
            return (T) contextOnlyMethod.invoke(this, contextOnly);
        }

        final MetaMethod argsOnlyMethod = this.findDoCreateMethod(args);
        if (argsOnlyMethod != null) {
            return (T) argsOnlyMethod.invoke(this, args);
        }

        throw new MissingMethodException(
                DO_CREATE,
                this.getClass(),
                args
        );
    }

    @Override
    public T create(ComponentContext componentContext, Object... args) {
        return this.findAndDoCreate(componentContext, args);
    }

}
