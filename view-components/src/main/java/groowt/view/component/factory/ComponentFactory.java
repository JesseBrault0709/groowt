package groowt.view.component.factory;

import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;

@FunctionalInterface
public interface ComponentFactory<T extends ViewComponent> {

    T create(String typeName, ComponentContext componentContext, Object... args);

    default T create(String alias, Class<?> type, ComponentContext componentContext, Object... args) {
        return this.create(type.getName(), componentContext, args);
    }

}
