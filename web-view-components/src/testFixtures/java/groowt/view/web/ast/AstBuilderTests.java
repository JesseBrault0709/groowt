package groowt.view.web.ast;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.node.Node;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AstBuilderTests {

    protected record BuildResult(Node node, TokenList tokenList) {}

    private static final Pattern withoutExtension = Pattern.compile("(?<name>.*)\\..+");

    protected static String getNameWithoutExtension(File file) {
        final var m = withoutExtension.matcher(file.getName());
        if (m.matches()) {
            return m.group("name");
        } else {
            throw new IllegalArgumentException("Cannot get name without extension for " + file);
        }
    }

    protected static String readFile(File file) {
        try (final var fis = new FileInputStream(file)) {
             return new String(fis.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Path sourceFileDir;
    private final String sourceFileGlob;
    private final File astTreeDir;
    private final String astFileSuffixAndExt;

    public AstBuilderTests(Path sourceFileDir, String sourceFileGlob, File astTreeDir, String astFileSuffixAndExt) {
        this.sourceFileDir = sourceFileDir;
        this.sourceFileGlob = sourceFileGlob;
        this.astTreeDir = astTreeDir;
        this.astFileSuffixAndExt = astFileSuffixAndExt;
    }

    protected abstract BuildResult buildFromSource(String source);

    protected abstract String format(BuildResult buildResult);

    protected void doSourceFileTest(String source, String expected) {
        final BuildResult buildResult = this.buildFromSource(source);
        final var actual = this.format(buildResult);
        assertEquals(expected, actual);
    }

    @TestFactory
    public Collection<DynamicTest> getSourceFileTests() {
        final var fs = FileSystems.getDefault();
        final PathMatcher matcher = fs.getPathMatcher(
                "glob:" + this.sourceFileDir.toString() + File.separator + this.sourceFileGlob
        );
        try (final Stream<Path> paths = Files.walk(this.sourceFileDir)) {
            return paths.filter(matcher::matches)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .map(file -> {
                        final var name = getNameWithoutExtension(file);
                        final var expectedAstFile = new File(this.astTreeDir, name + this.astFileSuffixAndExt);
                        final var source = readFile(file);
                        final var expected = readFile(expectedAstFile);
                        return DynamicTest.dynamicTest(
                                name,
                                () -> this.doSourceFileTest(source, expected)
                        );
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
