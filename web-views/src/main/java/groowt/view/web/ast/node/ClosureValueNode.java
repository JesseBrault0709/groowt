package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.GroovyCodeNodeExtension;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClosureValueNode extends AbstractLeafNode implements ValueNode {

    private final int groovyIndex;
    private final GroovyCodeNodeExtension groovyCode;

    @Inject
    public ClosureValueNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int groovyIndex
    ) {
        super(tokenRange, extensionContainer);
        this.groovyIndex = groovyIndex;
        this.groovyCode = this.createGroovyCode(tokenList);
    }

    protected GroovyCodeNodeExtension createGroovyCode(TokenList tokenList) {
        return this.createExtension(
                GroovyCodeNodeExtension.class,
                TokenRange.fromIndex(tokenList, this.getGroovyIndex()),
                (Function<? super List<Token>, String>) this::toValidGroovyCode
        );
    }

    protected String toValidGroovyCode(List<Token> groovyTokens) {
        return "def c = { " + groovyTokens.stream().map(Token::getText).collect(Collectors.joining()) + "\n}";
    }

    public GroovyCodeNodeExtension getGroovyCode() {
        return this.groovyCode;
    }

    public int getGroovyIndex() {
        return this.groovyIndex;
    }

}
