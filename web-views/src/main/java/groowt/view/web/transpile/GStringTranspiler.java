package groowt.view.web.transpile;

import groowt.view.web.ast.node.GStringBodyTextNode;
import groowt.view.web.ast.node.Node;
import org.codehaus.groovy.ast.expr.GStringExpression;

import java.util.List;

public interface GStringTranspiler {
    GStringExpression createGStringExpression(GStringBodyTextNode parent);
}
