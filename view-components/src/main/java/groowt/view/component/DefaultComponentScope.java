package groowt.view.component;

import java.util.HashMap;
import java.util.Map;

public class DefaultComponentScope implements ComponentScope {

    private final Map<String, ComponentFactory<?>> factories = new HashMap<>();

    @Override
    public void add(String name, ComponentFactory<?> factory) {
        this.factories.put(name, factory);
    }

    @Override
    public boolean contains(String name) {
        return this.factories.containsKey(name);
    }

    @Override
    public void remove(String name) {
        this.factories.remove(name);
    }

    @Override
    public ComponentFactory<?> get(String name) {
        return this.factories.get(name);
    }

}
