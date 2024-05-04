package groowt.view.component;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;

import java.util.function.Supplier;

@FunctionalInterface
public interface ComponentFactory<T extends ViewComponent> {

    /**
     * @param closure A closure with the following signature:
     * <p>
     * {@code Object componentType, ComponentContext context, ... -> T }
     * <p>
     * where '{@code ...}' represents any additional parameters (or none).
     * <p>
     * The first two parameters are not optional and must be present
     * or else this method will not work. The first parameter may be either
     * a {@link String} or a {@link Class}.
     *
     * @return A factory which will create type {@code T}.
     * @param <T> The desired {@link ViewComponent} type.
     */
    static <T extends ViewComponent> ComponentFactory<T> ofClosure(Closure<T> closure) {
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
