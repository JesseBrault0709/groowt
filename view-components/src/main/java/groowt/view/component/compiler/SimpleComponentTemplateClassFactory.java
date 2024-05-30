package groowt.view.component.compiler;

import groovy.lang.GroovyClassLoader;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.compiler.util.GroovyClassWriter;
import groowt.view.component.compiler.util.SimpleGroovyClassWriter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public final class SimpleComponentTemplateClassFactory implements ComponentTemplateClassFactory {

    private final Map<String, Class<? extends ComponentTemplate>> cache = new HashMap<>();
    private final ClassLoader classLoader;
    private final File tempClassesDir;
    private final GroovyClassWriter groovyClassWriter;

    public SimpleComponentTemplateClassFactory() {
        this(new GroovyClassLoader());
    }

    public SimpleComponentTemplateClassFactory(GroovyClassLoader groovyClassLoader) {
        this.groovyClassWriter = new SimpleGroovyClassWriter();
        try {
            this.tempClassesDir = Files.createTempDirectory("view-component-classes-").toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            groovyClassLoader.addURL(this.tempClassesDir.toURI().toURL());
            this.classLoader = groovyClassLoader;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<? extends ComponentTemplate> getTemplateClass(ComponentTemplateCompileResult compileResult) {
        final String templateClassName = compileResult.getTemplateClass().getName();
        if (this.cache.containsKey(templateClassName)) {
            return this.cache.get(templateClassName);
        } else {
            // write classes to disk
            this.groovyClassWriter.writeTo(this.tempClassesDir, compileResult.getTemplateClass());
            compileResult.getOtherClasses().forEach(groovyClass -> this.groovyClassWriter.writeTo(
                    this.tempClassesDir, groovyClass
            ));
            // load the template class
            try {
                @SuppressWarnings("unchecked")
                final var templateClass = (Class<? extends ComponentTemplate>) this.classLoader.loadClass(
                        templateClassName
                );
                this.cache.put(templateClassName, templateClass);
                return templateClass;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
