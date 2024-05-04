package groowt.view.component;

import groovy.lang.Closure;

import static groowt.view.component.ComponentFactoryUtil.flatten;

final class ClosureComponentFactory<T extends ViewComponent> extends ComponentFactoryBase<T> {

    private final Closure<T> closure;
    private final Class<?> firstParamType;

    public ClosureComponentFactory(Closure<T> closure) {
        this.closure = closure;
        if (this.closure.getParameterTypes().length < 2) {
            throw new IllegalArgumentException(
                    "Closures for " + getClass().getName() + " require at least two parameters"
            );
        }
        this.firstParamType = this.closure.getParameterTypes()[0];
        if (this.firstParamType != Object.class
                && !(this.firstParamType == String.class || this.firstParamType == Class.class)) {
            throw new IllegalArgumentException(
                    "The first closure parameter must be any of type Object (i.e, dynamic), String, or Class"
            );
        }
        final var secondParamType = this.closure.getParameterTypes()[1];
        if (secondParamType != Object.class && !ComponentContext.class.isAssignableFrom(secondParamType)) {
            throw new IllegalArgumentException(
                    "The second closure parameter must be of type Object (i.e., dynamic) or " +
                            "ComponentContext or a subclass thereof."
            );
        }
    }

    @Override
    public T create(String type, ComponentContext componentContext, Object... args) {
        if (this.firstParamType != Object.class && this.firstParamType != String.class) {
            throw new IllegalArgumentException("Cannot call this ClosureComponentFactory " +
                    "with a String component type argument.");
        }
        return this.closure.call(flatten(type, componentContext, args));
    }

    @Override
    public T create(Class<?> type, ComponentContext componentContext, Object... args) {
        if (this.firstParamType != Object.class && this.firstParamType != Class.class) {
            throw new IllegalArgumentException("Cannot call this ClosureComponentFactory " +
                    "with a Class component type argument.");
        }
        return this.closure.call(flatten(type, componentContext, args));
    }

}
