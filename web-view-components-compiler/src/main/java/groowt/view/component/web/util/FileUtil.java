package groowt.view.component.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class FileUtil {

    public static String readFile(File file) {
        try (final var fis = new FileInputStream(file)) {
            return new String(fis.readAllBytes());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private FileUtil() {}

}
