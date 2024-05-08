package groowt.view.component.context;

import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentFactory;

public interface ComponentScope {

    record TypeAndFactory<T extends ViewComponent>(Class<? extends T> type, ComponentFactory<? extends T> factory) {}

    //---- string types

    <T extends ViewComponent> void add(String typeName, Class<T> forClass, ComponentFactory<? extends T> factory);

    boolean contains(String typeName);

    void remove(String typeName);

    TypeAndFactory<?> get(String typeName);

    default TypeAndFactory<?> factoryMissing(String typeName) throws ComponentResolveException {
        throw new FactoryMissingUnsupportedException(
                this.getClass().getName() + " does not support factoryMissing() for string types.",
                typeName
        );
    }

    //---- class types

    <T extends ViewComponent> void add(Class<T> forClass, ComponentFactory<? extends T> factory);

    <T extends ViewComponent> void add(
            Class<T> publicType,
            Class<? extends T> implementingType,
            ComponentFactory<? extends T> factory
    );

    boolean contains(Class<? extends ViewComponent> type);

    <T extends ViewComponent> TypeAndFactory<T> get(Class<T> type);

    void remove(Class<? extends ViewComponent> type);

    default <T extends ViewComponent> TypeAndFactory<?> factoryMissing(String typeName, Class<T> type)
            throws ComponentResolveException {
        throw new FactoryMissingUnsupportedException(
                this.getClass().getName() + " does not support factoryMissing() for class component types.",
                typeName,
                type
        );
    }

}
