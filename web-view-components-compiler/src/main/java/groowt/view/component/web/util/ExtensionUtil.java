package groowt.view.component.web.util;

import java.io.File;
import java.nio.file.Path;

public final class ExtensionUtil {

    public static String getNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getNameWithoutExtension(File file) {
        return getNameWithoutExtension(file.getName());
    }

    public static String getNameWithoutExtension(Path path) {
        return getNameWithoutExtension(path.getFileName().toString());
    }

    private ExtensionUtil() {}

}
