package groowt.view.component;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class CachingComponentTemplateCompiler extends AbstractComponentTemplateCompiler {

    private final Map<Class<? extends ViewComponent>, ComponentTemplate> cache = new HashMap<>();

    @Override
    protected final ComponentTemplate compile(
            TemplateSource source,
            Class<? extends ViewComponent> forClass,
            Reader sourceReader
    ) {
        return this.cache.computeIfAbsent(forClass, ignored -> this.doCompile(source, forClass, sourceReader));
    }

    protected abstract ComponentTemplate doCompile(
            TemplateSource source,
            Class<? extends ViewComponent> forClass,
            Reader sourceReader
    );

}
