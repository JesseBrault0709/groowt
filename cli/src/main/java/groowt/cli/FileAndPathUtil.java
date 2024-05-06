package groowt.cli;

import java.io.File;

public final class FileAndPathUtil {

    public static File packageNameToFile(String packageName) {
        return new File(packageName.replace(".", File.separator));
    }

    public static File resolve(File from, File to) {
        return from.toPath().resolve(to.toPath()).toFile();
    }

    private FileAndPathUtil() {}

}
