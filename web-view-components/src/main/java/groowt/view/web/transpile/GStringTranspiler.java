package groowt.view.web.transpile;

import groowt.view.web.ast.node.GStringBodyTextNode;
import org.codehaus.groovy.ast.expr.GStringExpression;

public interface GStringTranspiler {
    GStringExpression createGStringExpression(GStringBodyTextNode parent);
}
