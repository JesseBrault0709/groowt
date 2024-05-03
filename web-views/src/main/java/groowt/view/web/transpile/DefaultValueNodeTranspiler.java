package groowt.view.web.transpile;

import groowt.view.web.ast.node.*;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import groowt.view.web.transpile.util.GroovyUtil;
import groowt.view.web.transpile.util.GroovyUtil.ConvertResult;
import jakarta.inject.Inject;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.jetbrains.annotations.Nullable;

import static groowt.view.web.transpile.TranspilerUtil.makeStringLiteral;

public class DefaultValueNodeTranspiler implements ValueNodeTranspiler {

    private final ComponentTranspiler componentTranspiler;

    public DefaultValueNodeTranspiler(ComponentTranspiler componentTranspiler) {
        this.componentTranspiler = componentTranspiler;
    }

    protected ClosureExpression closureValue(ClosureValueNode closureValueNode) {
        final var rawCode = closureValueNode.getGroovyCode().getAsValidGroovyCode();
        final ConvertResult convertResult = GroovyUtil.convert(rawCode);
        final @Nullable BlockStatement blockStatement = convertResult.blockStatement();
        if (blockStatement == null || blockStatement.isEmpty()) {
            throw new IllegalStateException("block statement is null or empty");
        }
        final ExpressionStatement exprStmt = (ExpressionStatement) blockStatement.getStatements().getFirst();
        return (ClosureExpression) exprStmt.getExpression();
    }

    private Expression gStringValue(GStringValueNode gStringValueNode) {
        final var rawCode = gStringValueNode.getGroovyCode().getAsValidGroovyCode();
        final ConvertResult convertResult = GroovyUtil.convert(rawCode);
        final @Nullable BlockStatement blockStatement = convertResult.blockStatement();
        if (blockStatement == null || blockStatement.isEmpty()) {
            throw new IllegalStateException("block statement is null or empty");
        }
        final ExpressionStatement exprStmt = (ExpressionStatement) blockStatement.getStatements().getFirst();
        return exprStmt.getExpression();
    }

    private ConstantExpression jStringValue(JStringValueNode jStringValueNode) {
        return makeStringLiteral(jStringValueNode.getContent());
    }

    private ClosureExpression emptyClosureValue(EmptyClosureValueNode emptyClosureValueNode) {
        return new ClosureExpression(Parameter.EMPTY_ARRAY, EmptyStatement.INSTANCE);
    }

    private ClosureExpression componentValue(ComponentValueNode componentValueNode, TranspilerState state) {
        return new ClosureExpression(
                Parameter.EMPTY_ARRAY,
                this.componentTranspiler.createComponentStatements(
                        componentValueNode.getComponentNode(),
                        state
                )
        );
    }

    @Override
    public Expression createExpression(ValueNode valueNode, TranspilerState state) {
        return switch (valueNode) {
            case ClosureValueNode closureValueNode -> this.closureValue(closureValueNode);
            case GStringValueNode gStringValueNode -> this.gStringValue(gStringValueNode);
            case JStringValueNode jStringValueNode -> this.jStringValue(jStringValueNode);
            case EmptyClosureValueNode emptyClosureValueNode -> this.emptyClosureValue(emptyClosureValueNode);
            case ComponentValueNode componentValueNode -> this.componentValue(componentValueNode, state);
            default -> throw new IllegalArgumentException(
                    "Unsupported ValueNode type: " + valueNode.getClass()
            );
        };
    }

}
