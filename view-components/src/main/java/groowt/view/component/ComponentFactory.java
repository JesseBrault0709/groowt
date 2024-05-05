package groowt.view.component;

import groovy.lang.Closure;

import java.util.function.Supplier;

@FunctionalInterface
public interface ComponentFactory<T extends ViewComponent> {

    static <T extends ViewComponent> ComponentFactory<T> ofClosure(Closure<? extends T> closure) {
        return new ClosureComponentFactory<>(closure);
    }

    static <T extends ViewComponent> ComponentFactory<T> ofSupplier(Supplier<T> supplier) {
        return new SupplierComponentFactory<>(supplier);
    }

    T create(String typeName, ComponentContext componentContext, Object... args);

    default T create(Class<?> type, ComponentContext componentContext, Object... args) {
        return this.create(type.getName(), componentContext, args);
    }

}
