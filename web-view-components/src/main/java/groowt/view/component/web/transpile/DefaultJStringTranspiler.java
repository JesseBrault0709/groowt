package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.JStringBodyTextNode;
import groowt.view.component.web.ast.node.JStringValueNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.codehaus.groovy.ast.expr.ConstantExpression;

import static org.apache.groovy.parser.antlr4.util.StringUtils.*;

@Singleton
public class DefaultJStringTranspiler implements JStringTranspiler {

    private final PositionSetter positionSetter;

    @Inject
    public DefaultJStringTranspiler(PositionSetter positionSetter) {
        this.positionSetter = positionSetter;
    }

    @Override
    public ConstantExpression createStringLiteral(JStringBodyTextNode jStringBodyTextNode) {
        final var withoutCR = removeCR(jStringBodyTextNode.getContent());
        final var escaped = replaceEscapes(withoutCR, NONE_SLASHY);
        final var expression = new ConstantExpression(escaped);
        expression.setNodeMetaData("_IS_STRING", true);
        this.positionSetter.setPosition(expression, jStringBodyTextNode);
        return expression;
    }

    @Override
    public ConstantExpression createStringLiteral(JStringValueNode jStringValueNode) {
        final var content = jStringValueNode.getContent();
        final var escaped = replaceEscapes(content, NONE_SLASHY);
        final var expression = new ConstantExpression(escaped);
        expression.setNodeMetaData("_IS_STRING", true);
        this.positionSetter.setPosition(expression, jStringValueNode);
        return expression;
    }

    @Override
    public ConstantExpression createEmptyStringLiteral() {
        final var expression = new ConstantExpression("");
        expression.setNodeMetaData("_IS_STRING", true);
        return expression;
    }

}
