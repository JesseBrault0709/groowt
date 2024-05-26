package groowt.view.component.web.transpile;

import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.BodyNode;
import groowt.view.component.web.ast.node.BodyTextNode;
import groowt.view.component.web.ast.node.ComponentNode;
import groowt.view.component.web.ast.node.Node;
import org.codehaus.groovy.ast.stmt.BlockStatement;

public class DefaultBodyTranspiler implements BodyTranspiler {

    private final ComponentTranspiler componentTranspiler;
    private final BodyTextTranspiler bodyTextTranspiler;

    public DefaultBodyTranspiler(ComponentTranspiler componentTranspiler, BodyTextTranspiler bodyTextTranspiler) {
        this.componentTranspiler = componentTranspiler;
        this.bodyTextTranspiler = bodyTextTranspiler;
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
                case ComponentNode componentNode ->
                        block.addStatements(this.componentTranspiler.createComponentStatements(componentNode, state));
                case BodyTextNode bodyTextNode ->
                        block.addStatements(this.bodyTextTranspiler.createBodyTextStatements(bodyTextNode, state));
                default -> throw new WebViewComponentBugError(new UnsupportedOperationException(
                        "BodyNode child of type " + child.getClass().getSimpleName() + " is not supported."
                ));
            }
        }
        state.popScope();
        return block;
    }

}
