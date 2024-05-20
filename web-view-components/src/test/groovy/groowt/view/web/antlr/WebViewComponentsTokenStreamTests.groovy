package groowt.view.web.antlr

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.Token
import org.junit.jupiter.api.Test

import static groowt.view.web.antlr.TokenUtil.getTokenName
import static groowt.view.web.antlr.WebViewComponentsLexer.GroovyCode
import static groowt.view.web.antlr.WebViewComponentsLexer.PreambleBreak
import static org.antlr.v4.runtime.Recognizer.EOF
import static org.junit.jupiter.api.Assertions.*

class WebViewComponentsTokenStreamTests {

    private static CharStream fromResource(String name) {
        CharStreams.fromStream(WebViewComponentsTokenStreamTests.getResourceAsStream(name))
    }

    private static void assertType(int expectedType, Token actual) {
        assertEquals(expectedType, actual.type) {
            "expected ${getTokenName(expectedType)} but got ${getTokenName(actual)}; " +
                    "actual.tokenIndex: ${actual.tokenIndex}"
        }
    }

    private static void assertTypes(List<Integer> expected, List<Token> actual) {
        if (expected.size() != actual.size()) {
            fail("expected.size() and actual.size() differ: ${expected.size()}, ${actual.size()}")
        }
        expected.eachWithIndex { expectedType, index ->
            assertType(expectedType, actual[index])
        }
    }

    private static void assertMergedGroovyCodeToken(
            Token token,
            @ClosureParams(value = SimpleType, options = ['MergedGroovyCodeToken'])
            Closure onSuccess = {}
    ) {
        assertInstanceOf(MergedGroovyCodeToken, token)
        onSuccess(token)
    }

    @Test
    void mergesGroovyTokens() {
        def input = fromResource('mergesGroovyTokens.gst')
        def lexer = new WebViewComponentsLexer(input)
        def tokenStream = new WebViewComponentsTokenStream(lexer)
        def tokens = tokenStream.allTokens
        assertTypes([PreambleBreak, GroovyCode, PreambleBreak, EOF], tokens)
        assertMergedGroovyCodeToken(tokens[1]) {
            assertEquals('println \'Hello, World!\' // comment\n', it.text)
        }
        assertIterableEquals(0..3, tokens*.tokenIndex)
    }

}
