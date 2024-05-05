package groowt.view.component.factory;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MissingMethodException;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.ViewComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be used to create custom implementations {@link ComponentFactory}.
 *
 * @implSpec All implementations must simply provide one or more {@code doCreate()} methods,
 * which will be found by this class via the Groovy meta object protocol. The method(s) may
 * have any of the following signatures:
 * <ul>
 *     <li>{@code String | Class, ComponentContext, ... -> T}</li>
 *     <li>{@code ComponentContext, ... -> T}</li>
 *     <li>{@code String | Class, ... -> T}</li>
 *     <li>{@code ... -> }</li>
 * </ul>
 * where '{@code ...}' represents zero or more additional arguments.
 *
 * @implNote In most cases, the implementation does not need to consume the
 * {@link ComponentContext} argument, as the compiled template is required to contain
 * {@code component.setContext(context)} statements following the component
 * creation call. However, if the component <strong>needs</strong> the context
 * (for example, to do custom scope logic), then it is more than okay to consume it.
 *
 * @implNote In the case Web View Components, the first additional argument will be
 * a {@link Map} containing the attributes of the component, followed by any additional
 * component constructor args.
 *
 * @param <T> The type of the ViewComponent produced by this factory.
 */
public abstract class ComponentFactoryBase<T extends ViewComponent> extends GroovyObjectSupport
        implements ComponentFactory<T> {

    protected static final String DO_CREATE = "doCreate";

    protected static MetaMethod findDoCreateMethod(MetaClass metaClass, Class<?>[] types) {
        return metaClass.getMetaMethod(DO_CREATE, types);
    }

    protected final Map<Class<?>[], MetaMethod> cache = new HashMap<>();

    protected MetaMethod findDoCreateMethod(Object[] allArgs) {
        return this.cache.computeIfAbsent(ComponentFactoryUtil.asTypes(allArgs), types ->
                findDoCreateMethod(this.getMetaClass(), types)
        );
    }

    @SuppressWarnings("unchecked")
    protected T findAndDoCreate(Object type, ComponentContext componentContext, Object[] args) {
        final Object[] typeContextAndArgs = ComponentFactoryUtil.flatten(type, componentContext, args);
        final MetaMethod typeContextAndArgsMethod = this.findDoCreateMethod(typeContextAndArgs);
        if (typeContextAndArgsMethod != null) {
            return (T) typeContextAndArgsMethod.invoke(this, typeContextAndArgs);
        }

        final Object[] typeAndContext = new Object[] { type, componentContext };
        final MetaMethod typeAndContextMethod = this.findDoCreateMethod(typeAndContext);
        if (typeAndContextMethod != null) {
            return (T) typeAndContextMethod.invoke(this, typeAndContext);
        }

        final Object[] typeAndArgs = ComponentFactoryUtil.flatten(type, args);
        final MetaMethod typeAndArgsMethod = this.findDoCreateMethod(typeAndArgs);
        if (typeAndArgsMethod != null) {
            return (T) typeAndArgsMethod.invoke(this, typeAndArgs);
        }

        final Object[] typeOnly = new Object[] { type };
        final MetaMethod typeOnlyMethod = this.findDoCreateMethod(typeOnly);
        if (typeOnlyMethod != null) {
            return (T) typeOnlyMethod.invoke(this, typeOnly);
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

        throw new MissingMethodException(DO_CREATE, this.getClass(), args);
    }

    @Override
    public T create(String type, ComponentContext componentContext, Object... args) {
        return this.findAndDoCreate(type, componentContext, args);
    }

    @Override
    public T create(Class<?> type, ComponentContext componentContext, Object... args) {
        return this.findAndDoCreate(type, componentContext, args);
    }

}
