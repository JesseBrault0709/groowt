package groowt.view.component.compiler.util;

import org.codehaus.groovy.tools.GroovyClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static groowt.view.component.compiler.util.ClassNameUtil.*;

public final class SimpleGroovyClassWriter implements GroovyClassWriter {

    @Override
    public void writeTo(File base, GroovyClass groovyClass) {
        final String className = groovyClass.getName();
        final List<String> packageDirParts = classNameToPackageDirParts(className);
        final File packageDir = resolvePackageDir(base, packageDirParts);
        if (!packageDir.exists() && !packageDir.mkdirs()) {
            throw new RuntimeException(new IOException("Could not make package dir(s) at " + packageDir));
        }
        final var classFile = new File(packageDir, isolateClassName(className) + ".class");
        try (final var fos = new FileOutputStream(classFile)) {
            fos.write(groovyClass.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
