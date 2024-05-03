package groowt.view.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.web.ast.extension.GroovyCodeNodeExtension;
import groowt.view.web.ast.extension.NodeExtensionContainer;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.function.Function;

public class EmptyClosureValueNode extends AbstractLeafNode implements ValueNode {

    private final GroovyCodeNodeExtension groovyCode;

    @Inject
    public EmptyClosureValueNode(NodeExtensionContainer extensionContainer, @Given TokenRange tokenRange) {
        super(tokenRange, extensionContainer);
        this.groovyCode = this.createGroovyCode();
    }

    protected GroovyCodeNodeExtension createGroovyCode() {
        return this.createExtension(
                GroovyCodeNodeExtension.class,
                TokenRange.empty(),
                (Function<? super List<Token>, String>) this::toValidGroovyCode
        );
    }

    public GroovyCodeNodeExtension getGroovyCode() {
        return this.groovyCode;
    }

    protected String toValidGroovyCode(List<Token> tokens) {
        return "{ }";
    }

}
