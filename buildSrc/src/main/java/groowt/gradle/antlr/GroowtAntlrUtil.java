package groowt.gradle.antlr;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class GroowtAntlrUtil {

    public static final List<String> antlrFileExtensions = List.of("g4", "g");

    private static final Pattern extensionPattern = Pattern.compile("(?<name>.*)\\.(?<ext>.*)$");

    public static File resolve(File from, File to) {
        return from.toPath().resolve(to.toPath()).toFile();
    }

    public static File relativize(File from, File to) {
        return from.toPath().relativize(to.toPath()).toFile();
    }

    public static boolean isAntlrSourceFile(File file) {
        final var m = extensionPattern.matcher(file.getName());
        if (m.matches()) {
            return antlrFileExtensions.contains(m.group("ext"));
        } else {
            throw new IllegalArgumentException("Cannot determine extension of file: " + file);
        }
    }

    public static List<String> sourceFileToIdentifierParts(File sourceDir, File sourceFile) {
        final var relative = getRelativePathToSourceFile(sourceDir, sourceFile);
        final List<String> result = new ArrayList<>();
        for (int i = 0; i < relative.getNameCount(); i++) {
            final var name = relative.getName(i);
            final var m = extensionPattern.matcher(name.toString());
            if (m.matches()) {
                result.add(m.group("name"));
            } else {
                result.add(name.toString());
            }
        }
        return result;
    }

    private static Path getRelativePathToSourceFile(File sourceDir, File sourceFile) {
        if (!sourceDir.isAbsolute()) {
            throw new IllegalArgumentException("sourceDir must be absolute, given: " + sourceDir);
        }
        if (sourceFile.isAbsolute()) {
            final var sourceDirPath = sourceDir.toPath();
            final var sourceFilePath = sourceFile.toPath();
            return sourceDirPath.relativize(sourceFilePath);
        } else {
            return sourceFile.toPath();
        }
    }

    public static String getSourceIdentifier(ResolvedSource resolvedSource) {
        final List<String> parts = new ArrayList<>();
        if (!resolvedSource.getSourceSet().getName().equals("main")) {
            parts.add(resolvedSource.getSourceSet().getName());
        }
        parts.addAll(sourceFileToIdentifierParts(resolvedSource.getSourceDir(), resolvedSource.getSourceFile()));
        final List<String> capitalizedParts = parts.stream()
                .map(part -> {
                    final var first = part.substring(0, 1);
                    final var rest = part.substring(1);
                    return first.toUpperCase() + rest;
                })
                .toList();
        return String.join("", capitalizedParts);
    }

    public static Provider<Directory> getOutputDirectory(
            Project project,
            SourceSet sourceSet,
            Provider<String> packageNameProvider
    ) {
        return project.getLayout().getBuildDirectory().flatMap(buildDir -> {
            return buildDir.dir(packageNameProvider.map(packageName -> {
                return String.join(File.separator, List.of(
                        "generated-src",
                        "antlr",
                        sourceSet.getName(),
                        packageName.replace(".", File.separator)
                ));
            }));
        });
    }

    public static String getGenerateTaskName(SourceSpec sourceSpec) {
        return sourceSpec.getResolvedSource().getSourceSet().getTaskName("generate", sourceSpec.getIdentifier());
    }

    public static String getGenerateAllTaskName(SourceSet sourceSet) {
        return sourceSet.getTaskName("generate", "AllAntlr");
    }

    public static ResolvedSource resolveSource(
            Project project,
            SourceSet sourceSet,
            SourceDirectorySet sourceDirectorySet,
            File sourceFile
    ) {
        if (!isAntlrSourceFile(sourceFile)) {
            throw new IllegalArgumentException(
                    "The given source file " + sourceFile + " is not a recognized antlr file (bad extension)."
            );
        }

        final List<File> potentialSrcDirs = new ArrayList<>();

        if (sourceFile.isAbsolute()) {
            for (final File srcDir : sourceDirectorySet.getSrcDirs()) {
                if (sourceFile.getPath().startsWith(srcDir.getPath())) {
                    potentialSrcDirs.add(srcDir);
                }
            }
        } else {
            for (final File srcDir : sourceDirectorySet.getSrcDirs()) {
                if (resolve(srcDir, sourceFile).exists()) {
                    potentialSrcDirs.add(srcDir);
                }
            }
        }

        if (potentialSrcDirs.size() > 1) {
            throw new IllegalArgumentException("Multiple source directories in " + sourceDirectorySet.getName() + " contain a source file " + sourceFile);
        } else if (potentialSrcDirs.isEmpty()) {
            throw new IllegalArgumentException("No directories in " + sourceDirectorySet.getName() + " contain a source file " + sourceFile);
        }

        final File srcDir = potentialSrcDirs.getFirst();
        return new ResolvedSource(project, sourceSet, srcDir, resolve(srcDir, sourceFile));
    }

    private GroowtAntlrUtil() {}

}
