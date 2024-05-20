package groowt.view.web.ast;

import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;

import java.io.File;
import java.nio.file.Path;

public class DefaultAstBuilderTests extends AstBuilderTests {

    public DefaultAstBuilderTests() {
        super(
                Path.of(String.join(File.separator, "src", "test", "ast")),
                "*.wvc",
                new File(String.join(File.separator, "src", "test", "ast", "trees")),
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
