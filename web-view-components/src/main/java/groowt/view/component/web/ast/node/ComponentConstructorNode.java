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

public class ComponentConstructorNode extends AbstractLeafNode {

    private final int groovyCodeIndex;
    private final GroovyCodeNodeExtension groovyCode;

    @Inject
    public ComponentConstructorNode(
            TokenList tokenList,
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            @Given int groovyCodeIndex
    ) {
        super(tokenRange, extensionContainer);
        this.groovyCodeIndex = groovyCodeIndex;
        this.groovyCode = this.createGroovyCode(tokenList);
    }

    protected GroovyCodeNodeExtension createGroovyCode(TokenList tokenList) {
        return this.createExtension(
                GroovyCodeNodeExtension.class,
                TokenRange.fromIndex(tokenList, this.groovyCodeIndex),
                (Function<? super List<Token>, String>) this::toValidGroovyCode
        );
    }

    protected String toValidGroovyCode(List<Token> tokens) {
        final var rawCode = tokens.stream().map(Token::getText).collect(Collectors.joining()).trim();
        if (rawCode.isEmpty() || rawCode.isBlank()) {
            throw new IllegalStateException("Raw code cannot be blank or empty.");
        }
        return "[" + rawCode + ']';
    }

    public int getGroovyCodeIndex() {
        return this.groovyCodeIndex;
    }

    public GroovyCodeNodeExtension getGroovyCode() {
        return this.groovyCode;
    }

}
