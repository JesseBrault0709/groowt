package groowt.view.component.web.ast.extension;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.node.Node;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GroovyCodeNodeExtension implements NodeExtension {

    private final Node self;
    private final List<Token> groovyCodeTokens;
    private final Function<? super List<Token>, String> toValidGroovyCode;

    @Inject
    public GroovyCodeNodeExtension(
            TokenList allTokens,
            @SelfNode Node self,
            @Given TokenRange groovyCodeTokenRange,
            @Given Function<? super List<Token>, String> toValidGroovyCode
    ) {
        this.self = self;
        this.groovyCodeTokens = allTokens.getRange(groovyCodeTokenRange);
        this.toValidGroovyCode = toValidGroovyCode;
    }

    @Override
    public Node getSelf() {
        return this.self;
    }

    /**
     * @return A copy of the tokens list.
     */
    public List<Token> getGroovyCodeTokens() {
        return new ArrayList<>(this.groovyCodeTokens);
    }

    public String getAsValidGroovyCode() {
        return this.toValidGroovyCode.apply(this.getGroovyCodeTokens());
    }

}
