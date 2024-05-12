package groowt.view.component.compiler.util;

import org.codehaus.groovy.tools.GroovyClass;

import java.io.File;
import java.nio.file.Path;

public interface GroovyClassWriter {

    void writeTo(File base, GroovyClass groovyClass);

    default void writeTo(Path base, GroovyClass groovyClass) {
        this.writeTo(base.toFile(), groovyClass);
    }

}
