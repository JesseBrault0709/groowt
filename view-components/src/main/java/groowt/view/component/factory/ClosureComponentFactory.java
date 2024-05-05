package groowt.view.component.factory;

import groovy.lang.Closure;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.ViewComponent;

import static groowt.view.component.factory.ComponentFactoryUtil.flatten;

final class ClosureComponentFactory<T extends ViewComponent> implements ComponentFactory<T> {

    private enum Type {
        ALL,
        NAME_AND_CONTEXT, NAME_AND_ARGS, CONTEXT_AND_ARGS,
        NAME_ONLY, CONTEXT_ONLY, ARGS_ONLY,
        NONE
    }

    private final Closure<T> closure;
    private final Type type;

    @SuppressWarnings("unchecked")
    public ClosureComponentFactory(Closure<? extends T> closure) {
        this.closure = (Closure<T>) closure;
        final var paramTypes = this.closure.getParameterTypes();
        if (paramTypes.length == 0) {
            this.type = Type.NONE;
        } else if (paramTypes.length == 1) {
            final var paramType = paramTypes[0];
            if (paramType == String.class || paramType == Class.class) {
                this.type = Type.NAME_ONLY;
            } else if (ComponentContext.class.isAssignableFrom(paramType)) {
                this.type = Type.CONTEXT_ONLY;
            } else {
                this.type = Type.ARGS_ONLY;
            }
        } else {
            final var firstParamType = paramTypes[0];
            final var secondParamType = paramTypes[1];
            if (firstParamType == String.class || firstParamType == Class.class) {
                if (ComponentContext.class.isAssignableFrom(secondParamType)) {
                    if (paramTypes.length > 2) {
                        this.type = Type.ALL;
                    } else {
                        this.type = Type.NAME_AND_CONTEXT;
                    }
                } else {
                    this.type = Type.NAME_AND_ARGS;
                }
            } else if (ComponentContext.class.isAssignableFrom(firstParamType)) {
                this.type = Type.CONTEXT_AND_ARGS;
            } else {
                this.type = Type.ARGS_ONLY;
            }
        }
    }

    private T flatCall(Object... args) {
        return this.closure.call(flatten(args));
    }

    private T objTypeCreate(Object type, ComponentContext componentContext, Object... args) {
        return switch (this.type) {
            case ALL -> this.flatCall(type, componentContext, args);
            case NAME_AND_CONTEXT -> this.closure.call(type, componentContext);
            case NAME_AND_ARGS -> this.flatCall(type, args);
            case CONTEXT_AND_ARGS -> this.flatCall(componentContext, args);
            case NAME_ONLY -> this.closure.call(type);
            case CONTEXT_ONLY -> this.closure.call(componentContext);
            case ARGS_ONLY -> this.closure.call(args);
            case NONE -> this.closure.call();
        };
    }

    @Override
    public T create(String type, ComponentContext componentContext, Object... args) {
        return this.objTypeCreate(type, componentContext, args);
    }

    @Override
    public T create(Class<?> type, ComponentContext componentContext, Object... args) {
        return this.objTypeCreate(type, componentContext, args);
    }

}
