package groowt.view.component.web.ast;

import groowt.view.component.web.ast.node.Node;
import org.antlr.v4.runtime.ParserRuleContext;

public class DefaultAstBuilder implements AstBuilder {

    private final NodeFactory nodeFactory;

    public DefaultAstBuilder(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Node build(ParserRuleContext ruleContext) {
        return ruleContext.accept(new DefaultAstBuilderVisitor(this.nodeFactory));
    }

}
