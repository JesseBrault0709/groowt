package groowt.view.component.factory;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;

import java.util.function.Supplier;

public final class ComponentFactories {

    public static <T extends ViewComponent> ComponentFactory<T> ofClosureStringType(Closure<? extends T> closure) {
        return new StringTypeClosureComponentFactory<>(closure);
    }

    public static <T extends ViewComponent> ComponentFactory<T> ofClosureClassType(
            Class<T> forClass,
            Closure<? extends T> closure
    ) {
        return new ClassTypeClosureComponentFactory<>(forClass, closure);
    }

    public static <T extends ViewComponent> ComponentFactory<T> ofSupplier(Supplier<? extends T> supplier) {
        return (typeName, componentContext, args) -> supplier.get();
    }

    public static <T extends ViewComponent> ComponentFactory<T> ofNoArgConstructor(
            Class<? extends T> viewComponentClass
    ) {
        return (typeName, componentContext, args) -> {
            try {
                return viewComponentClass.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private ComponentFactories() {}

}
