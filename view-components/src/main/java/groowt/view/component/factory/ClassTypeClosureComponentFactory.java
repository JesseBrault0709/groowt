package groowt.view.component.factory;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;

final class ClassTypeClosureComponentFactory<T extends ViewComponent> extends AbstractClosureComponentFactory<T> {

    private final Class<T> forClass;

    public ClassTypeClosureComponentFactory(Class<T> forClass, Closure<? extends T> closure) {
        super(closure);
        this.forClass = forClass;
    }

    @Override
    public T create(String typeName, ComponentContext componentContext, Object... args) {
        throw new UnsupportedOperationException(
                "ClassTypeClosureComponentFactory cannot handle string component types."
        );
    }

    @Override
    public T create(String alias, Class<?> type, ComponentContext componentContext, Object... args) {
        if (!this.forClass.isAssignableFrom(type)) {
            throw new IllegalArgumentException(
                    "This ClassTypeClosureComponentFactory cannot handle type " + type.getName()
                            + "; can only handle " + this.forClass.getName() + "."
            );
        }
        return this.doCall(alias, componentContext, args);
    }

}
