package groowt.view.component;

import groovy.lang.Closure;

final class ClosureComponentFactory<T extends ViewComponent> extends AbstractComponentFactory<T> {

    private final Closure<T> closure;

    public ClosureComponentFactory(Closure<T> closure) {
        this.closure = closure;
    }

    @Override
    public T create(Object type, ComponentContext componentContext, Object... args) {
        final Object[] flattened = ComponentFactoryUtil.flatten(type, componentContext, args);
        return this.closure.call(flattened);
    }

}
