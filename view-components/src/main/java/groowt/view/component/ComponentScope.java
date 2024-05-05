package groowt.view.component;

import groovy.lang.Closure;

public interface ComponentScope {

    void add(String name, ComponentFactory<?> factory);
    boolean contains(String name);
    void remove(String name);
    ComponentFactory<?> get(String name);

    default ComponentFactory<?> factoryMissing(String typeName) {
        throw new NoFactoryMissingException(this.getClass().getName() + " does not support factoryMissing()");
    }

    default <T extends ViewComponent> void add(Class<T> clazz, ComponentFactory<T> factory) {
        this.add(clazz.getName(), factory);
    }

    default boolean contains(Class<? extends ViewComponent> clazz) {
        return this.contains(clazz.getName());
    }

    @SuppressWarnings("unchecked")
    default <T extends ViewComponent> ComponentFactory<T> get(Class<T> clazz) {
        return (ComponentFactory<T>) this.get(clazz.getName());
    }

    default void remove(Class<? extends ViewComponent> clazz) {
        this.remove(clazz.getName());
    }

    @SuppressWarnings("unchecked")
    default <T extends ViewComponent> ComponentFactory<T> factoryMissing(Class<T> clazz) {
        return (ComponentFactory<T>) this.factoryMissing(clazz.getName());
    }

    default void add(String name, Closure<? extends ViewComponent> closure) {
        this.add(name, ComponentFactory.ofClosure(closure));
    }

    default <T extends ViewComponent> void add(Class<T> type, Closure<? extends T> closure) {
        this.add(type, ComponentFactory.ofClosure(closure));
    }

}
