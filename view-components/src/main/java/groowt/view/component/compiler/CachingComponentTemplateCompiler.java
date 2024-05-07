package groowt.view.component.compiler;

import groovy.lang.GroovyClassLoader;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentTemplateSource;
import org.codehaus.groovy.tools.GroovyClass;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class CachingComponentTemplateCompiler extends AbstractComponentTemplateCompiler {

    private record CachedTemplate(
            ComponentTemplateCompileResult compileResult,
            @Nullable ComponentTemplate template
    ) {}

    private final Map<Class<? extends ViewComponent>, CachedTemplate> cache = new HashMap<>();

    public CachingComponentTemplateCompiler(GroovyClassLoader groovyClassLoader) {
        super(groovyClassLoader);
    }

    private ComponentTemplate instantiate(
            GroovyClassLoader groovyClassLoader,
            ComponentTemplateCompileResult compileResult
    ) {
        for (final var groovyClass : compileResult.otherClasses()) {
            // Try to find it. If we can't, we need to load it via the groovy loader
            try {
                Class.forName(groovyClass.getName(), true, groovyClassLoader);
            } catch (ClassNotFoundException ignored) {
                groovyClassLoader.defineClass(groovyClass.getName(), groovyClass.getBytes());
            } catch (LinkageError ignored) {
                // no-op, because we already have it
            }
        }
        final GroovyClass templateGroovyClass = compileResult.templateClass();
        Class<?> templateClass;
        // Try to find it. If we can't, we need to load it via the groovy loader
        try {
            templateClass = Class.forName(templateGroovyClass.getName(), true, groovyClassLoader);
        } catch (ClassNotFoundException ignored) {
            templateClass = groovyClassLoader.defineClass(
                    templateGroovyClass.getName(),
                    templateGroovyClass.getBytes()
            );
        }
        try {
            return (ComponentTemplate) templateClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate ComponentTemplate " + templateClass.getName(), e);
        }
    }

    @Override
    public final ComponentTemplate compileAndGet(
            GroovyClassLoader groovyClassLoader,
            Class<? extends ViewComponent> forClass,
            ComponentTemplateSource source
    ) throws ComponentTemplateCompileErrorException {
        if (this.cache.containsKey(forClass)) {
            final var cached = this.cache.get(forClass);
            if (cached.template() == null) {
                final ComponentTemplate template = this.instantiate(groovyClassLoader, cached.compileResult());
                this.cache.put(forClass, new CachedTemplate(cached.compileResult(), template));
                return template;
            } else {
                return cached.template();
            }
        } else {
            final ComponentTemplateCompileResult compileResult = this.compile(forClass, source);
            final ComponentTemplate template = this.instantiate(groovyClassLoader, compileResult);
            this.cache.put(forClass, new CachedTemplate(compileResult, template));
            return template;
        }
    }

    @Override
    protected final ComponentTemplateCompileResult compile(
            ComponentTemplateSource componentTemplateSource,
            Class<? extends ViewComponent> forClass,
            Reader actualSource
    ) throws ComponentTemplateCompileErrorException {
        if (this.cache.containsKey(forClass)) {
            return this.cache.get(forClass).compileResult();
        } else {
            final ComponentTemplateCompileResult compileResult =
                    this.doCompile(componentTemplateSource, forClass, actualSource);
            this.cache.put(forClass, new CachedTemplate(compileResult, null));
            return compileResult;
        }
    }

    protected abstract ComponentTemplateCompileResult doCompile(
            ComponentTemplateSource source,
            Class<? extends ViewComponent> forClass,
            Reader reader
    ) throws ComponentTemplateCompileErrorException;

}
