package groowt.view.component.context;

import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultComponentScope implements ComponentScope {

    private final Map<String, TypeAndFactory<?>> stringFactories = new HashMap<>();
    private final Map<Class<?>, TypeAndFactory<?>> classFactories = new HashMap<>();

    @Override
    public <T extends ViewComponent> void add(String typeName, Class<T> forClass, ComponentFactory<? extends T> factory) {
        this.stringFactories.put(typeName, new TypeAndFactory<>(forClass, factory));
    }

    @Override
    public boolean contains(String typeName) {
        return this.stringFactories.containsKey(typeName);
    }

    @Override
    public TypeAndFactory<?> get(String typeName) {
        return Objects.requireNonNull(this.stringFactories.get(typeName));
    }

    @Override
    public void remove(String typeName) {
        this.stringFactories.remove(typeName);
    }

    @Override
    public <T extends ViewComponent> void add(Class<T> forClass, ComponentFactory<? extends T> factory) {
        this.classFactories.put(forClass, new TypeAndFactory<>(forClass, factory));
    }

    @Override
    public <T extends ViewComponent> void add(
            Class<T> publicType,
            Class<? extends T> implementingType,
            ComponentFactory<? extends T> factory
    ) {
        this.classFactories.put(publicType, new TypeAndFactory<T>(implementingType, factory));
    }

    @Override
    public boolean contains(Class<? extends ViewComponent> type) {
        return this.classFactories.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewComponent> TypeAndFactory<T> get(Class<T> type) {
        return (TypeAndFactory<T>) Objects.requireNonNull(this.classFactories.get(type));
    }

    @Override
    public void remove(Class<? extends ViewComponent> type) {
        this.classFactories.remove(type);
    }

}
