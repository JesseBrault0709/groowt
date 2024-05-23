package groowt.view.component.web.antlr;

import groowt.view.component.web.testutil.FileComparisonTestUtil;
import groowt.view.component.web.util.ExtensionUtil;
import groowt.view.component.web.util.FileUtil;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class WebViewComponentsLexerTests {

    @TestFactory
    public Collection<DynamicTest> lexerFileTests() {
        return FileComparisonTestUtil.getTestsFor(
                Path.of("src", "test", "lexer"),
                "*.wvc",
                Path.of("src", "test", "lexer", "tokens-files"),
                sourcePath -> {
                    final String nameWithoutExtension = ExtensionUtil.getNameWithoutExtension(sourcePath);
                    return Path.of(nameWithoutExtension + "_tokens.txt");
                },
                sourceFile -> {
                    final CharStream input = CharStreams.fromString(FileUtil.readFile(sourceFile));
                    final WebViewComponentsLexer lexer = new WebViewComponentsLexer(input);
                    // include all (!) (non-skipped) tokens for testing via Set.of()
                    final WebViewComponentsTokenStream tokenStream = new WebViewComponentsTokenStream(lexer, Set.of());
                    final List<Token> allTokens = tokenStream.getAllTokensSkipEOF();
                    final var sb = new StringBuilder();
                    for (int i = 0; i < allTokens.size(); i++) {
                        sb.append(i).append(": ").append(TokenUtil.formatToken(allTokens.get(i)));
                        if (i < allTokens.size() - 1) {
                            sb.append("\n");
                        }
                    }
                    return sb.toString();
                }
        );
    }

}
