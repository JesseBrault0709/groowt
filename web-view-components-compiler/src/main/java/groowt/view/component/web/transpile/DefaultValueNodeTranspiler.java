package groowt.view.component.web.transpile;

import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.*;
import groowt.view.component.web.transpile.groovy.GroovyUtil;
import groowt.view.component.web.transpile.groovy.GroovyUtil.ConvertResult;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static groowt.view.component.web.transpile.TranspilerUtil.getStringLiteral;

// TODO: set positions
public class DefaultValueNodeTranspiler implements ValueNodeTranspiler {

    private final ComponentTranspiler componentTranspiler;

    public DefaultValueNodeTranspiler(ComponentTranspiler componentTranspiler) {
        this.componentTranspiler = componentTranspiler;
    }

    // TODO: positions
    protected Expression handleClosureNode(ClosureValueNode closureValueNode) {
        final var rawCode = closureValueNode.getGroovyCode().getAsValidGroovyCode();
        final ClosureExpression convertedClosure = GroovyUtil.getClosure(rawCode);
        final Statement closureCode = convertedClosure.getCode();
        if (closureCode instanceof BlockStatement blockStatement) {
            final List<Statement> statements = blockStatement.getStatements();
            if (statements.isEmpty()) {
                throw new WebViewComponentBugError(new IllegalArgumentException(
                        "Did not expect ClosureValueNode to produce no statements."
                ));
            } else if (statements.size() == 1) {
                final Statement statement = statements.getFirst();
                if (statement instanceof ExpressionStatement expressionStatement) {
                    final Expression expression = expressionStatement.getExpression();
                    return switch (expression) {
                        case ConstantExpression ignored -> expression;
                        case VariableExpression ignored -> expression;
                        case PropertyExpression ignored -> expression;
                        default -> convertedClosure;
                    };
                } else {
                    throw new IllegalArgumentException("A component closure value must produce a value.");
                }
            } else {
                return convertedClosure;
            }
        } else {
            return convertedClosure;
        }
    }

    private Expression gStringValue(GStringValueNode gStringValueNode) {
        final var rawCode = gStringValueNode.getGroovyCode().getAsValidGroovyCode();
        final ConvertResult convertResult = GroovyUtil.convert(rawCode);
        final @Nullable BlockStatement blockStatement = convertResult.blockStatement();
        if (blockStatement == null || blockStatement.isEmpty()) {
            throw new IllegalStateException("block statement is null or empty");
        }
        final ExpressionStatement exprStmt = (ExpressionStatement) blockStatement.getStatements().getFirst();
        // TODO: set pos
        return exprStmt.getExpression();
    }

    private ConstantExpression jStringValue(JStringValueNode jStringValueNode) {
        return getStringLiteral(jStringValueNode.getContent()); // TODO: set pos
    }

    private ClosureExpression emptyClosureValue(EmptyClosureValueNode emptyClosureValueNode) {
        return new ClosureExpression(Parameter.EMPTY_ARRAY, EmptyStatement.INSTANCE); // TODO: set pos
    }

    private ClosureExpression componentValue(ComponentValueNode componentValueNode, TranspilerState state) {
        return new ClosureExpression(
                Parameter.EMPTY_ARRAY,
                new BlockStatement(this.componentTranspiler.createComponentStatements(
                        componentValueNode.getComponentNode(),
                        state
                ), state.getCurrentScope())
        ); // TODO: set pos
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
