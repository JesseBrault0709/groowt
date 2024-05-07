package groowt.view.component.compiler;

import groovy.lang.GroovyClassLoader;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentTemplateSource;
import org.codehaus.groovy.tools.GroovyClass;

import java.io.IOException;
import java.util.List;

public interface ComponentTemplateCompiler {

    record ComponentTemplateCompileResult(GroovyClass templateClass, List<GroovyClass> otherClasses) {}

    ComponentTemplateCompileResult compile(Class<? extends ViewComponent> forClass, ComponentTemplateSource source)
            throws ComponentTemplateCompileErrorException;

    ComponentTemplate compileAndGet(
            GroovyClassLoader groovyClassLoader,
            Class<? extends ViewComponent> forClass,
            ComponentTemplateSource source
    ) throws ComponentTemplateCompileErrorException;

    default ComponentTemplate compileAndGet(Class<? extends ViewComponent> forClass, ComponentTemplateSource source)
            throws ComponentTemplateCompileErrorException {
        try (final GroovyClassLoader groovyClassLoader = new GroovyClassLoader(this.getClass().getClassLoader())) {
            return this.compileAndGet(groovyClassLoader, forClass, source);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

}
