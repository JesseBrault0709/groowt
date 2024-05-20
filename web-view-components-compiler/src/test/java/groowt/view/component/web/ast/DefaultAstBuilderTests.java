package groowt.view.component.web.ast;

import groowt.view.component.web.antlr.ParserUtil;
import groowt.view.component.web.antlr.TokenList;

import java.nio.file.Path;

public class DefaultAstBuilderTests extends AstBuilderTests {

    public DefaultAstBuilderTests() {
        super(
                Path.of("src", "test", "ast"),
                "*.wvc",
                Path.of("src", "test", "ast", "trees"),
                "_ast.txt"
        );
    }

    @Override
    protected BuildResult buildFromSource(String source) {
        final var parseResult = ParserUtil.parseCompilationUnit(source);
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var b = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        return new BuildResult(b.build(parseResult.getCompilationUnitContext()), tokenList);
    }

    @Override
    protected String format(BuildResult buildResult) {
        return NodeUtil.formatAst(buildResult.node(), buildResult.tokenList());
    }

}
