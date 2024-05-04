package groowt.view.component;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaMethod;
import groovy.lang.MissingMethodException;

import java.util.*;

public abstract class AbstractComponentFactory<T extends ViewComponent> extends GroovyObjectSupport
        implements ComponentFactory<T> {

    protected final Map<Class<?>[], MetaMethod> cache = new HashMap<>();

    protected MetaMethod findDoCreateMethod(Object[] allArgs) {
        return this.cache.computeIfAbsent(ComponentFactoryUtil.asTypes(allArgs), types ->
                ComponentFactoryUtil.findDoCreateMethod(this.getMetaClass(), types)
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

        throw new MissingMethodException(
                ComponentFactoryUtil.DO_CREATE,
                this.getClass(),
                args
        );
    }

    @Override
    public T create(Object type, ComponentContext componentContext, Object... args) {
        return this.findAndDoCreate(type, componentContext, args);
    }

}
