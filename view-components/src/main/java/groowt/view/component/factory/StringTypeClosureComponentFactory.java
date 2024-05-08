package groowt.view.component.factory;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;

final class StringTypeClosureComponentFactory<T extends ViewComponent> extends AbstractClosureComponentFactory<T> {

    public StringTypeClosureComponentFactory(Closure<? extends T> closure) {
        super(closure);
    }

    @Override
    public T create(String typeName, ComponentContext componentContext, Object... args) {
        return this.doCall(typeName, componentContext, args);
    }

    @Override
    public T create(String alias, Class<?> type, ComponentContext componentContext, Object... args) {
        throw new UnsupportedOperationException(
                "StringTypeClosureComponentFactory cannot handle class component types."
        );
    }

}
