package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.extension.GroovyCodeNodeExtension;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PreambleNode extends AbstractLeafNode {

    private final GroovyCodeNodeExtension groovyCode;

    @Inject
    public PreambleNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int groovyCodeIndex
    ) {
        super(tokenRange, extensionContainer);
        this.groovyCode = this.createGroovyCode(tokenList, groovyCodeIndex);
    }

    protected GroovyCodeNodeExtension createGroovyCode(TokenList tokenList, int groovyCodeIndex) {
        return this.createExtension(
                GroovyCodeNodeExtension.class,
                TokenRange.fromIndex(tokenList, groovyCodeIndex),
                (Function<? super List<Token>, String>) this::toValidGroovyCode
        );
    }

    protected String toValidGroovyCode(List<Token> tokenList) {
        return tokenList.stream().map(Token::getText).collect(Collectors.joining());
    }

    public GroovyCodeNodeExtension getGroovyCode() {
        return this.groovyCode;
    }

}
