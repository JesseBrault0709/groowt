package groowt.view.component.web.testutil;

import groowt.view.component.web.util.ExtensionUtil;
import groowt.view.component.web.util.FileUtil;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DynamicTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public final class FileComparisonTestUtil {

    public static void doComparisonTest(File expected, String actualText) {
        if (!expected.isFile()) {
            throw new IllegalArgumentException("expected is not a file or does not exist: " + expected);
        }
        final String expectedText = FileUtil.readFile(expected);
        assertEquals(expectedText, actualText);
    }

    public static Collection<DynamicTest> getTestsFor(
            Path inputDirectory,
            String inputFilesGlob,
            Path outputDirectory,
            Function<Path, @Nullable Path> inputFileToExpected,
            Function<File, String> inputToActual
    ) {
        final Collection<DynamicTest> result = new ArrayList<>();
        final FileSystem fileSystem = FileSystems.getDefault();
        final PathMatcher matcher = fileSystem.getPathMatcher("glob:" + inputDirectory.resolve(inputFilesGlob));
        try (final var files = Files.walk(inputDirectory)) {
            files.filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .forEach(inputPath -> {
                        final @Nullable Path expected = inputFileToExpected.apply(inputPath);
                        if (expected != null) {
                            final File expectedResolvedFile = outputDirectory.resolve(expected).toFile();
                            result.add(DynamicTest.dynamicTest(ExtensionUtil.getNameWithoutExtension(inputPath), () -> {
                                final String actual = inputToActual.apply(inputPath.toFile());
                                doComparisonTest(expectedResolvedFile, actual);
                            }));
                        }
                    });
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
        return result;
    }

    private FileComparisonTestUtil() {}

}
