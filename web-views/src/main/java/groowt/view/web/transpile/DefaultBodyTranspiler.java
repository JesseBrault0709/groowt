package groowt.view.web.transpile;

import groowt.view.web.ast.node.*;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class DefaultBodyTranspiler implements BodyTranspiler {

    private final GStringTranspiler gStringTranspiler;
    private final JStringTranspiler jStringTranspiler;
    private final ComponentTranspiler componentTranspiler;

    @Inject
    public DefaultBodyTranspiler(
            GStringTranspiler gStringTranspiler,
            JStringTranspiler jStringTranspiler,
            ComponentTranspiler componentTranspiler
    ) {
        this.gStringTranspiler = gStringTranspiler;
        this.jStringTranspiler = jStringTranspiler;
        this.componentTranspiler = componentTranspiler;
    }

    @Override
    public BlockStatement transpileBody(
            BodyNode bodyNode,
            ExpressionStatementConverter converter,
            TranspilerState state
    ) {
        final BlockStatement block = new BlockStatement();
        block.setVariableScope(state.currentScope());
        for (final Node child : bodyNode.getChildren()) {
            switch (child) {
                case GStringBodyTextNode gStringBodyTextNode -> {
                    final GStringExpression gString = this.gStringTranspiler.createGStringExpression(
                            gStringBodyTextNode
                    );
                    block.addStatement(converter.createStatement(gStringBodyTextNode, gString));
                }
                case JStringBodyTextNode jStringBodyTextNode -> {
                    block.addStatement(
                            converter.createStatement(
                                    jStringBodyTextNode,
                                    this.jStringTranspiler.createStringLiteral(jStringBodyTextNode)
                            )
                    );
                }
                case ComponentNode componentNode -> {
                    final BlockStatement componentBlock = this.componentTranspiler.createComponentStatements(
                            componentNode,
                            state
                    );
                    block.addStatement(componentBlock);
                }
                case PlainScriptletNode plainScriptletNode -> {
                    throw new UnsupportedOperationException("TODO");
                }
                default -> throw new UnsupportedOperationException(
                        "BodyNode child of type " + child.getClass().getSimpleName() + " is not supported."
                );
            }
        }
        return block;
    }

}
