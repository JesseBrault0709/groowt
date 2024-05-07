package groowt.view.component.compiler;

import groovy.lang.GroovyClassLoader;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentTemplateSource;

import java.io.IOException;
import java.io.Reader;

public abstract class AbstractComponentTemplateCompiler implements ComponentTemplateCompiler {

    private final GroovyClassLoader groovyClassLoader;

    public AbstractComponentTemplateCompiler(GroovyClassLoader groovyClassLoader) {
        this.groovyClassLoader = groovyClassLoader;
    }

    protected abstract ComponentTemplateCompileResult compile(
            ComponentTemplateSource componentTemplateSource,
            Class<? extends ViewComponent> forClass,
            Reader actualSource
    ) throws ComponentTemplateCompileErrorException;

    @Override
    public ComponentTemplateCompileResult compile(Class<? extends ViewComponent> forClass, ComponentTemplateSource source)
            throws ComponentTemplateCompileErrorException {
        try (final Reader reader = ComponentTemplateSource.toReader(source)) {
            return this.compile(source, forClass, reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ComponentTemplate compileAndGet(Class<? extends ViewComponent> forClass, ComponentTemplateSource source)
            throws ComponentTemplateCompileErrorException {
        return this.compileAndGet(this.groovyClassLoader, forClass, source);
    }

}
