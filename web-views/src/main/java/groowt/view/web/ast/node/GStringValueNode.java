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

public class GStringValueNode extends AbstractLeafNode implements ValueNode {

    private final GroovyCodeNodeExtension groovyCode;

    @Inject
    public GStringValueNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int contentIndex
    ) {
        super(tokenRange, extensionContainer);
        this.groovyCode = this.createGroovyCode(tokenList, contentIndex);
    }

    protected GroovyCodeNodeExtension createGroovyCode(TokenList tokenList, int contentIndex) {
        return this.createExtension(
                GroovyCodeNodeExtension.class,
                TokenRange.fromIndex(tokenList, contentIndex),
                (Function<? super List<Token>, String>) this::toValidGroovyCode
        );
    }

    protected String toValidGroovyCode(List<Token> tokens) {
        return "\"" + tokens.stream().map(Token::getText).collect(Collectors.joining()) + "\"";
    }

    public GroovyCodeNodeExtension getGroovyCode() {
        return this.groovyCode;
    }

}
