package groowt.view.component.web.ast;

import groowt.view.component.web.antlr.ParserUtil;
import groowt.view.component.web.antlr.TokenList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static groowt.view.component.web.antlr.LexerErrorKt.formatLexerError;
import static groowt.view.component.web.antlr.ParserErrorKt.formatParserError;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultAstBuilderTests extends AstBuilderTests {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAstBuilderTests.class);

    public DefaultAstBuilderTests() {
        super(
                Path.of("src", "test", "ast"),
                "*.wvc",
                Path.of("src", "test", "ast", "ast-files"),
                "_ast.txt"
        );
    }

    @Override
    protected BuildResult buildFromSource(String source) {
        final var parseResult = ParserUtil.parseCompilationUnit(source);

        if (!parseResult.getLexerErrors().isEmpty()) {
            parseResult.getLexerErrors().forEach(error -> {
                logger.error(formatLexerError(error));
            });
            fail("There were lexer errors. See log for more information.");
        }
        if (!parseResult.getParserErrors().isEmpty()) {
            parseResult.getParserErrors().forEach(error -> {
                logger.error(formatParserError(error));
            });
            fail("There were parser errors. See log for more information.");
        }

        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var b = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        return new BuildResult(b.build(parseResult.getCompilationUnitContext()), tokenList);
    }

    @Override
    protected String format(BuildResult buildResult) {
        return NodeUtil.formatAst(buildResult.node(), buildResult.tokenList());
    }

}
