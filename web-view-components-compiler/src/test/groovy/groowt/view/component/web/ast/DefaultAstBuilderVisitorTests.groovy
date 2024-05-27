package groowt.view.component.web.ast

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam
import groowt.view.component.web.antlr.TokenList
import groowt.view.component.web.antlr.WebViewComponentsLexer
import groowt.view.component.web.antlr.WebViewComponentsParser
import groowt.view.component.web.antlr.WebViewComponentsTokenStream
import groowt.view.component.web.ast.node.Node
import org.antlr.v4.runtime.CharStreams

import static groowt.view.component.web.antlr.WebViewComponentsParser.CompilationUnitContext
import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertNotNull

class DefaultAstBuilderVisitorTests {

    private static <T extends Node> void assertNodeWith(
            @DelegatesTo.Target Class<T> expectedType,
            Node actual,
            @ClosureParams(FirstParam.FirstGenericType)
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, genericTypeIndex = 0)
            Closure with
    ) {
        Objects.requireNonNull(expectedType)
        assertNotNull(actual)
        assertInstanceOf(expectedType, actual)
        with.delegate = actual
        with.resolveStrategy = Closure.DELEGATE_FIRST
        with.call(actual)
    }

    private static <T extends Node> void assertNode(Class<T> expectedType, Node actual) {
        assertNodeWith(expectedType, actual) { }
    }

    private Tuple2<CompilationUnitContext, TokenList> parse(String source) {
        def input = CharStreams.fromString(source)
        def lexer = new WebViewComponentsLexer(input)
        def tokenStream = new WebViewComponentsTokenStream(lexer)
        def parser = new WebViewComponentsParser(tokenStream)
        def cu = parser.compilationUnit()
        def tokenList = new TokenList(tokenStream)
        return new Tuple2<>(cu, tokenList)
    }

    private Tuple2<Node, TokenList> doBuild(String source) {
        def (cu, tokenList) = this.parse(source)
        def nodeFactory = new DefaultNodeFactory(tokenList)
        def visitor = new DefaultAstBuilderVisitor(nodeFactory)
        return new Tuple2<>(cu.accept(visitor), tokenList)
    }

}
