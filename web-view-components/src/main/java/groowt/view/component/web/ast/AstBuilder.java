package groowt.view.component.web.ast;

import groowt.view.component.web.antlr.WebViewComponentsParser.CompilationUnitContext;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import groowt.view.component.web.ast.node.Node;
import org.antlr.v4.runtime.ParserRuleContext;

public interface AstBuilder {

    Node build(ParserRuleContext ruleContext);

    default CompilationUnitNode buildCompilationUnit(CompilationUnitContext compilationUnitContext) {
        return (CompilationUnitNode) this.build(compilationUnitContext);
    }

}
