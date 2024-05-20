package groowt.view.component.web.ast.extension;

import groowt.view.component.web.ast.node.Node;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.stream.Collectors;

public abstract sealed class GStringNodeExtension implements NodeExtension
        permits GStringPathExtension, GStringScriptletExtension {

    private final Node self;
    private final List<Token> rawTokens;

    public GStringNodeExtension(Node self, List<Token> rawTokens) {
        this.self = self;
        this.rawTokens = rawTokens;
    }

    @Override
    public Node getSelf() {
        return this.self;
    }

    public List<Token> getRawTokens() {
        return this.rawTokens;
    }

    public String getAsValidEmbeddableCode() {
        return this.getRawTokens().stream().map(Token::getText).collect(Collectors.joining());
    }

}
