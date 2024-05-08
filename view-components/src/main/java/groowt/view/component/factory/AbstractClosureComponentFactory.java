package groowt.view.component.factory;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;

import static groowt.view.component.factory.ComponentFactoryUtil.flatten;

public abstract class AbstractClosureComponentFactory<T extends ViewComponent> extends Closure<T> implements
        ComponentFactory<T> {

    private enum Type {
        ALL,
        NAME_AND_CONTEXT, NAME_AND_ARGS, CONTEXT_AND_ARGS,
        NAME_ONLY, CONTEXT_ONLY, ARGS_ONLY,
        NONE
    }

    private final Closure<? extends T> closure;
    private final Type type;

    public AbstractClosureComponentFactory(Closure<? extends T> closure) {
        super(closure.getOwner(), closure.getThisObject());
        this.closure = closure;
        final var paramTypes = this.closure.getParameterTypes();
        if (paramTypes.length == 0) {
            this.type = Type.NONE;
        } else if (paramTypes.length == 1) {
            final var paramType = paramTypes[0];
            if (paramType == String.class) {
                this.type = Type.NAME_ONLY;
            } else if (ComponentContext.class.isAssignableFrom(paramType)) {
                this.type = Type.CONTEXT_ONLY;
            } else {
                this.type = Type.ARGS_ONLY;
            }
        } else {
            final var firstParamType = paramTypes[0];
            final var secondParamType = paramTypes[1];
            if (firstParamType == String.class) {
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

    protected T doCall(String typeNameOrAlias, ComponentContext componentContext, Object... args) {
        return switch (this.type) {
            case ALL -> this.closure.call(flatten(typeNameOrAlias, componentContext, args));
            case NAME_AND_CONTEXT -> this.closure.call(typeNameOrAlias, componentContext);
            case NAME_AND_ARGS -> this.closure.call(flatten(typeNameOrAlias, args));
            case CONTEXT_AND_ARGS -> this.closure.call(flatten(componentContext, args));
            case NAME_ONLY -> this.closure.call(typeNameOrAlias);
            case CONTEXT_ONLY -> this.closure.call(componentContext);
            case ARGS_ONLY -> this.closure.call(args);
            case NONE -> this.closure.call();
        };
    }

}
