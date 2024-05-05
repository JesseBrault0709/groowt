package groowt.view.component.compiler;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentTemplateSource;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class CachingComponentTemplateCompiler extends AbstractComponentTemplateCompiler {

    private final Map<Class<? extends ViewComponent>, ComponentTemplate> cache = new HashMap<>();

    @Override
    protected final ComponentTemplate compile(
            ComponentTemplateSource source,
            Class<? extends ViewComponent> forClass,
            Reader sourceReader
    ) {
        return this.cache.computeIfAbsent(forClass, ignored -> this.doCompile(source, forClass, sourceReader));
    }

    protected abstract ComponentTemplate doCompile(
            ComponentTemplateSource source,
            Class<? extends ViewComponent> forClass,
            Reader sourceReader
    );

}
