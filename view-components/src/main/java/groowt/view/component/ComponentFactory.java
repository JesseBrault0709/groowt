package groowt.view.component;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;

import java.util.function.Supplier;

public interface ComponentFactory<T extends ViewComponent> extends GroovyObject {

    static <T extends ViewComponent> ComponentFactory<T> ofClosure(Closure<T> closure) {
        return new ClosureComponentFactory<>(closure);
    }

    static <T extends ViewComponent> ComponentFactory<T> ofSupplier(Supplier<T> supplier) {
        return new SupplierComponentFactory<>(supplier);
    }

    T create(Object type, ComponentContext componentContext, Object... args);

}
