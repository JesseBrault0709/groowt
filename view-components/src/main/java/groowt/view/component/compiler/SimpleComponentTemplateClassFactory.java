package groowt.view.component.compiler;

import groowt.view.component.ComponentTemplate;
import org.codehaus.groovy.tools.GroovyClass;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

public final class SimpleComponentTemplateClassFactory implements ComponentTemplateClassFactory {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static String[] classNameToPackageDirParts(String fullClassName) {
        final String[] allParts = fullClassName.split("\\.");
        if (allParts.length == 0) {
            throw new RuntimeException("Did not expect allParts.length to be zero.");
        } else if (allParts.length == 1) {
            return EMPTY_STRING_ARRAY;
        } else {
            final var result = new String[allParts.length - 1];
            System.arraycopy(allParts, 0, result, 0, allParts.length - 1);
            return result;
        }
    }

    private static Path resolvePackageDir(Path rootDir, String[] packageDirParts) {
        return Path.of(rootDir.toString(), packageDirParts);
    }

    private static String isolateClassName(String fullClassName) {
        final String[] parts = fullClassName.split("\\.");
        if (parts.length == 0) {
            throw new RuntimeException("Did not expect parts.length to be zero");
        }
        return parts[parts.length - 1];
    }

    private final Map<String, Class<? extends ComponentTemplate>> cache = new HashMap<>();
    private final ClassLoader classLoader;
    private final Path tempClassesDir;

    public SimpleComponentTemplateClassFactory() {
        try {
            this.tempClassesDir = Files.createTempDirectory("view-component-classes-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            this.classLoader = new URLClassLoader(
                    "SimpleComponentTemplateClassFactoryClassLoader",
                    new URL[] { this.tempClassesDir.toUri().toURL() },
                    this.getClass().getClassLoader()
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeClassToDisk(GroovyClass groovyClass) {
        final var className = groovyClass.getName();
        final var packageDirParts = classNameToPackageDirParts(className);
        final var packageDir = resolvePackageDir(this.tempClassesDir, packageDirParts);
        try {
            Files.createDirectories(packageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final var classFile = Path.of(packageDir.toString(), isolateClassName(className) + ".class");
        try {
            Files.write(classFile, groovyClass.getBytes(), CREATE_NEW, WRITE);
        } catch (IOException e) {
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
            this.writeClassToDisk(compileResult.getTemplateClass());
            compileResult.getOtherClasses().forEach(this::writeClassToDisk);
            // load the template class
            try {
                //noinspection unchecked
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
