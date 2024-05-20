package groowt.view.component.web.transpile;

import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.*;
import groowt.view.component.web.transpile.TranspilerUtil.TranspilerState;
import jakarta.inject.Inject;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;

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
            AddOrAppendCallback addOrAppendCallback,
            TranspilerState state
    ) {
        final BlockStatement block = new BlockStatement();
        block.setVariableScope(state.pushScope());
        for (final Node child : bodyNode.getChildren()) {
            switch (child) {
                case GStringBodyTextNode gStringBodyTextNode -> {
                    final GStringExpression gString = this.gStringTranspiler.createGStringExpression(
                            gStringBodyTextNode
                    );
                    block.addStatement(addOrAppendCallback.createStatement(gStringBodyTextNode, gString));
                }
                case JStringBodyTextNode jStringBodyTextNode -> {
                    block.addStatement(
                            addOrAppendCallback.createStatement(
                                    jStringBodyTextNode,
                                    this.jStringTranspiler.createStringLiteral(jStringBodyTextNode)
                            )
                    );
                }
                case ComponentNode componentNode -> {
                    // DO NOT add/append this, because the component transpiler does it already
                    block.addStatements(this.componentTranspiler.createComponentStatements(componentNode, state));
                }
                case PlainScriptletNode plainScriptletNode -> {
                    throw new UnsupportedOperationException("TODO");
                }
                default -> throw new WebViewComponentBugError(new UnsupportedOperationException(
                        "BodyNode child of type " + child.getClass().getSimpleName() + " is not supported."
                ));
            }
        }
        state.popScope();
        return block;
    }

}
