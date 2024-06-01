package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.*;
import groowt.view.component.web.transpile.groovy.GroovyUtil;
import groowt.view.component.web.transpile.groovy.GroovyUtil.ConvertResult;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static groowt.view.component.web.transpile.TranspilerUtil.getStringLiteral;

public class DefaultValueNodeTranspiler implements ValueNodeTranspiler {

    private final ComponentTranspiler componentTranspiler;
    private final PositionSetter positionSetter;

    public DefaultValueNodeTranspiler(ComponentTranspiler componentTranspiler, PositionSetter positionSetter) {
        this.componentTranspiler = componentTranspiler;
        this.positionSetter = positionSetter;
    }

    protected Expression handleClosureNode(ClosureValueNode closureValueNode) {
        final var rawCode = closureValueNode.getGroovyCode().getAsValidGroovyCode();
        final ClosureExpression convertedClosure = GroovyUtil.getClosure(rawCode);

        final PositionVisitor positionVisitor = new PositionVisitor(
                this.positionSetter.withOffset(0, -10),
                closureValueNode
        );
        convertedClosure.visit(positionVisitor);

        final @Nullable Parameter[] closureParams = convertedClosure.getParameters();
        if (closureParams != null && closureParams.length == 0) {
            final BlockStatement closureCode = (BlockStatement) convertedClosure.getCode();
            final List<Statement> statements = closureCode.getStatements();
            if (statements.size() == 1 && statements.getFirst() instanceof ExpressionStatement expressionStatement) {
                return expressionStatement.getExpression();
            }
        }
        return convertedClosure;
    }

    private Expression gStringValue(GStringValueNode gStringValueNode) {
        final var rawCode = gStringValueNode.getGroovyCode().getAsValidGroovyCode();
        final ConvertResult convertResult = GroovyUtil.convert(rawCode);
        final @Nullable BlockStatement blockStatement = convertResult.blockStatement();
        if (blockStatement == null || blockStatement.isEmpty()) {
            throw new IllegalStateException("block statement is null or empty");
        }
        final ExpressionStatement exprStmt = (ExpressionStatement) blockStatement.getStatements().getFirst();

        final PositionVisitor positionVisitor = new PositionVisitor(
                this.positionSetter.withOffset(0, -1),
                gStringValueNode
        );
        exprStmt.visit(positionVisitor);

        return exprStmt.getExpression();
    }

    private ConstantExpression jStringValue(JStringValueNode jStringValueNode) {
        final ConstantExpression literal = getStringLiteral(jStringValueNode.getContent());
        this.positionSetter.setPosition(literal, jStringValueNode);
        return literal;
    }

    private ClosureExpression emptyClosureValue(EmptyClosureValueNode emptyClosureValueNode) {
        final ClosureExpression cl = new ClosureExpression(Parameter.EMPTY_ARRAY, EmptyStatement.INSTANCE);
        final PositionVisitor positionVisitor = new PositionVisitor(this.positionSetter, emptyClosureValueNode);
        cl.visit(positionVisitor);
        return cl;
    }

    private ClosureExpression componentValue(ComponentValueNode componentValueNode, TranspilerState state) {
        return new ClosureExpression(
                Parameter.EMPTY_ARRAY,
                new BlockStatement(this.componentTranspiler.createComponentStatements(
                        componentValueNode.getComponentNode(),
                        state,
                        true
                ), state.getCurrentScope())
        );
    }

    @Override
    public Expression createExpression(ValueNode valueNode, TranspilerState state) {
        return switch (valueNode) {
            case ClosureValueNode closureValueNode -> this.handleClosureNode(closureValueNode);
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
