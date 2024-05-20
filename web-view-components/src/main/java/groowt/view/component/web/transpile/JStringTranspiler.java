package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.JStringBodyTextNode;
import groowt.view.component.web.ast.node.JStringValueNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;

public interface JStringTranspiler {
    ConstantExpression createStringLiteral(JStringBodyTextNode bodyTextNode);
    ConstantExpression createStringLiteral(JStringValueNode jStringValueNode);
    ConstantExpression createEmptyStringLiteral();
}
