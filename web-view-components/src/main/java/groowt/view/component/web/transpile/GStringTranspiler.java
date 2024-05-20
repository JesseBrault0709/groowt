package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.GStringBodyTextNode;
import org.codehaus.groovy.ast.expr.GStringExpression;

public interface GStringTranspiler {
    GStringExpression createGStringExpression(GStringBodyTextNode parent);
}
