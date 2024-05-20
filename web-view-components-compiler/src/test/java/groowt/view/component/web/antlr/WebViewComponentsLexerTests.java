package groowt.view.component.web.antlr;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static groowt.view.component.web.antlr.WebViewComponentsLexer.*;
import static org.junit.jupiter.api.Assertions.*;

public class WebViewComponentsLexerTests {

    private static void assertTokenType(int type, Token token) {
        assertEquals(
                type,
                token.getType(),
                () -> "Expected " + VOCABULARY.getDisplayName(type)
                        + " but got " + VOCABULARY.getDisplayName(token.getType())
        );
    }

    @Test
    public void helloTarget() {
        final var input = CharStreams.fromString("Hello, $target!");
        final var lexer = new WebViewComponentsLexer(input);
        final var tokenStream = new WebViewComponentsTokenStream(lexer);
        final var allTokens = tokenStream.getAllTokens();
        assertEquals(5, allTokens.size(), () -> {
            return "Wrong number of tokens; tokens: " + allTokens.stream()
                    .map(Token::toString)
                    .collect(Collectors.joining(", "));
        });
        final var t0 = allTokens.get(0);
        final var t1 = allTokens.get(1);
        final var t2 = allTokens.get(2);
        final var t3 = allTokens.get(3);
        final var t4 = allTokens.get(4);
        assertEquals("Hello, ", t0.getText());
        assertTokenType(RawText, t0);
        assertEquals("$", t1.getText());
        assertTokenType(DollarReferenceStart, t1);
        assertEquals("target", t2.getText());
        assertTokenType(GroovyCode, t2);
        assertInstanceOf(MergedGroovyCodeToken.class, t2);
        assertEquals("!", t3.getText());
        assertTokenType(RawText, t3);
        assertTokenType(EOF, t4);
    }

}
