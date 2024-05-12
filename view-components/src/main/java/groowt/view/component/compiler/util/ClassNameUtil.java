package groowt.view.component.compiler.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class ClassNameUtil {

    public static List<String> classNameToPackageDirParts(String fullClassName) {
        final String[] allParts = fullClassName.split("\\.");
        if (allParts.length == 0) {
            throw new RuntimeException("Did not expect allParts.length to be zero.");
        } else if (allParts.length == 1) {
            return List.of();
        } else {
            return Arrays.asList(allParts).subList(0, allParts.length - 1);
        }
    }

    public static File resolvePackageDir(File rootDir, List<String> packageDirParts) {
        return new File(rootDir, String.join(File.separator, packageDirParts));
    }

    public static String isolateClassName(String fullClassName) {
        final String[] parts = fullClassName.split("\\.");
        if (parts.length == 0) {
            throw new RuntimeException("Did not expect parts.length to be zero");
        }
        return parts[parts.length - 1];
    }

    private ClassNameUtil() {}

}
