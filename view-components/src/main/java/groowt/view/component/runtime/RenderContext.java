package groowt.view.component.runtime;

import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentResolveException;
import groowt.view.component.factory.ComponentFactory;

import java.util.List;

public interface RenderContext {

    interface Resolved<T extends ViewComponent> {
        Class<? extends T> resolvedType();
        ComponentFactory<? extends T> componentFactory();
    }

    record ResolvedStringType<T extends ViewComponent>(
            String typeName,
            Class<? extends T> resolvedType,
            ComponentFactory<? extends T> componentFactory
    ) implements Resolved<T> {}

    record ResolvedClassType<T extends ViewComponent>(
            String alias,
            Class<T> requestedType,
            Class<? extends T> resolvedType,
            ComponentFactory<? extends T> componentFactory
    ) implements Resolved<T> {}

    Resolved<?> resolve(String typeName) throws ComponentResolveException;
    <T extends ViewComponent> Resolved<T> resolve(String alias, Class<T> type) throws ComponentResolveException;

    void pushComponent(ViewComponent component);
    void popComponent(ViewComponent component);

    List<ViewComponent> getComponentStack();

    ComponentWriter getWriter();

}
