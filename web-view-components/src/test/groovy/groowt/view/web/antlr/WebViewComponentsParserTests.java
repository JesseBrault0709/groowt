package groowt.view.web.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public final class WebViewComponentsParserTests {

    private static final String parserFileBase = String.join(File.separator, "src", "test", "parser");
    private static final String parserTreeFileBase = String.join(File.separator, parserFileBase, "trees");
    private static final String parseTreeFileSuffix = "_parseTree";
    private static final String parseTreeFileExtension = ".txt";
    private static final Set<String> parserFileGlobs = Set.of(
            String.join(File.separator, parserFileBase, "*.wvc")
    );
    private static final Pattern nameAndExtension = Pattern.compile("(?<name>.*)\\.(?<ext>.+)");

    private WebViewComponentsParser getParser(CharStream input) {
        final var lexer = new WebViewComponentsLexer(input);
        final var tokenStream = new WebViewComponentsTokenStream(lexer);
        return new WebViewComponentsParser(tokenStream);
    }

    private WebViewComponentsParser getParser(File file) {
        try {
            return this.getParser(CharStreams.fromFileName(file.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private WebViewComponentsParser getParser(String source) {
        return this.getParser(CharStreams.fromString(source));
    }

    private String getFileName(File parserFile) {
        final var m = nameAndExtension.matcher(parserFile.getName());
        if (m.matches()) {
            return m.group("name");
        } else {
            throw new IllegalArgumentException("Could not determine file name from: " + parserFile.getName());
        }
    }

    private Executable getParserFileTest(File parserFile) {
        return () -> {
            final var parser = this.getParser(parserFile);
            final var cu = parser.compilationUnit();
            assertTrue(AntlrUtil.findErrorNodes(cu).isEmpty(), () -> {
                final var formatted = ParserUtil.formatTree(parser, cu, true);
                return "Parse result had errors:\n" + formatted;
            });
            final var parseTreeFileName = this.getFileName(parserFile) + parseTreeFileSuffix + parseTreeFileExtension;
            final File parseTreeFile = new File(parserTreeFileBase, parseTreeFileName);
            if (!parseTreeFile.exists()) {
                throw new IllegalArgumentException(
                        "There is no parse tree file for " + parseTreeFileName + " at " + parseTreeFile
                );
            }
            try (final FileInputStream fis = new FileInputStream(parseTreeFile)) {
                final var expectedFormatted = new String(fis.readAllBytes());
                final var actualFormatted = ParserUtil.formatTree(parser, cu, false);
                assertEquals(expectedFormatted, actualFormatted);
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        };
    }

    @TestFactory
    public Collection<DynamicTest> getParserFileTests() {
        final Collection<DynamicTest> tests = new ArrayList<>();
        final FileSystem fs = FileSystems.getDefault();
        for (final var glob : parserFileGlobs) {
            final var matcher = fs.getPathMatcher("glob:" + glob);
            try (final var paths = Files.walk(Path.of(parserFileBase))) {
                paths.filter(matcher::matches).forEach(path -> {
                    final File asFile = path.toFile();
                    if (!asFile.isDirectory()) {
                        final DynamicTest test = DynamicTest.dynamicTest(
                                this.getFileName(asFile),
                                this.getParserFileTest(asFile)
                        );
                        tests.add(test);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tests;
    }

}
