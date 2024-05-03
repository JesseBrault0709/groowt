package groowt.view.component;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;

import java.util.function.Supplier;

public interface ComponentFactory<T extends ViewComponent> extends GroovyObject {

    static <T extends ViewComponent> ComponentFactory<T> of(Closure<T> closure) {
        return new DelegatingComponentFactory<>((context, args) -> closure.call(context, args));
    }

    static <T extends ViewComponent> ComponentFactory<T> of(Supplier<T> supplier) {
        return new DelegatingComponentFactory<>((ignored0, ignored1) -> supplier.get());
    }

    T create(ComponentContext componentContext, Object... args);

}
