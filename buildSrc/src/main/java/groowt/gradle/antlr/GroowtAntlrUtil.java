package groowt.gradle.antlr;

import org.gradle.api.tasks.SourceSet;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public final class GroowtAntlrUtil {

    public static final List<String> antlrExtensions = List.of("g4", "g");

    private static final Pattern nameAndExtension = Pattern.compile("(?<name>.*)\\.(?<ext>.*)$");

    public static boolean isAntlrFile(File file) {
        for (final var antlrExtension : antlrExtensions) {
            if (file.getName().endsWith(antlrExtension)) {
                return true;
            }
        }
        return false;
    }

    public static String getGenerateTaskName(SourceSet sourceSet, File sourceFile) {
        final var matcher = nameAndExtension.matcher(sourceFile.getName());
        if (matcher.matches()) {
            return sourceSet.getTaskName("generate", matcher.group("name"));
        } else {
            throw new IllegalArgumentException("Cannot determine source name for " + sourceFile);
        }
    }

    private GroowtAntlrUtil() {}

}
