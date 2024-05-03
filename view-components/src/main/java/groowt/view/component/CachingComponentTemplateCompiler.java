package groowt.view.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class CachingComponentTemplateCompiler implements ComponentTemplateCompiler {

    private final Map<Class<? extends ViewComponent>, ComponentTemplate> cache = new HashMap<>();

    protected final void putInCache(Class<? extends ViewComponent> forClass, ComponentTemplate template) {
        this.cache.put(forClass, template);
    }

    protected final ComponentTemplate getFromCache(Class<? extends ViewComponent> forClass) {
        return Objects.requireNonNull(this.cache.get(forClass));
    }

    protected final ComponentTemplate getFromCacheOrElse(
            Class<? extends ViewComponent> forClass,
            Supplier<? extends ComponentTemplate> onEmpty
    ) {
        return this.cache.computeIfAbsent(forClass, ignored -> onEmpty.get());
    }

}
