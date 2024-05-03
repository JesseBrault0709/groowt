package groowt.view.web.transpile;

import groowt.view.web.ast.node.JStringBodyTextNode;
import groowt.view.web.ast.node.JStringValueNode;
import groowt.view.web.ast.node.Node;
import org.codehaus.groovy.ast.expr.ConstantExpression;

public interface JStringTranspiler {
    ConstantExpression createStringLiteral(JStringBodyTextNode bodyTextNode);
    ConstantExpression createStringLiteral(JStringValueNode jStringValueNode);
    ConstantExpression createEmptyStringLiteral();
}
