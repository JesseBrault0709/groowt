package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.extension.GroovyCodeNodeExtension;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EqualsScriptletNode extends AbstractLeafNode implements GroovyBodyNode {

    private final int groovyIndex;
    private final GroovyCodeNodeExtension groovyCode;

    public EqualsScriptletNode(
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
                TokenRange.fromIndex(tokenList, this.groovyIndex),
                (Function<? super List<Token>, String>) this::toValidGroovyCode
        );
    }

    protected String toValidGroovyCode(List<Token> groovyTokens) {
        return "{ -> " + groovyTokens.stream().map(Token::getText).collect(Collectors.joining()) + " }";
    }

    public int getGroovyIndex() {
        return this.groovyIndex;
    }

    public GroovyCodeNodeExtension getGroovyCode() {
        return this.groovyCode;
    }

}
