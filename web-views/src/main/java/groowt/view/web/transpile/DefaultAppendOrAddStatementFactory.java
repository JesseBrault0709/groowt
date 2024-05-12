package groowt.view.web.transpile;

import groovy.lang.Tuple2;
import groowt.view.web.ast.node.BodyChildNode;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.function.Function;

public class DefaultAppendOrAddStatementFactory implements AppendOrAddStatementFactory {

    private void addLineAndColumn(
            BodyChildNode bodyChildNode,
            TupleExpression args
    ) {
        final Tuple2<ConstantExpression, ConstantExpression> lineAndColumn = TranspilerUtil.lineAndColumn(
                bodyChildNode.asNode().getTokenRange().getStartPosition()
        );
        args.addExpression(lineAndColumn.getV1());
        args.addExpression(lineAndColumn.getV2());
    }

    private Statement doCreate(
            BodyChildNode bodyChildNode,
            Expression rightSide,
            VariableExpression target,
            String methodName // ,
            // boolean addLineAndColumn
    ) {
        final ArgumentListExpression args;
        if (rightSide instanceof ArgumentListExpression argumentListExpression) {
            args = argumentListExpression;
        } else {
            args = new ArgumentListExpression();
            args.addExpression(rightSide);
        }
//        if (addLineAndColumn &&
//                NodeUtil.isAnyOfType(bodyChildNode.asNode(), GStringBodyTextNode.class, ComponentNode.class)) {
//            this.addLineAndColumn(bodyChildNode, args);
//        }
        final MethodCallExpression outExpression = new MethodCallExpression(target, methodName, args);
        return new ExpressionStatement(outExpression);
    }

    protected Statement addOnly(BodyChildNode bodyChildNode, TranspilerState state, Expression rightSide) {
        return this.doCreate(
                bodyChildNode,
                rightSide,
                state.getCurrentChildList(),
                TranspilerUtil.ADD //,
                // false
        );
    }

    protected Statement appendOnly(BodyChildNode bodyChildNode, TranspilerState state, Expression rightSide) {
        return this.doCreate(
                bodyChildNode,
                rightSide,
                state.getWriter(),
                TranspilerUtil.APPEND //,
                // false
        );
    }

    @Override
    public Statement addOrAppend(
            BodyChildNode bodyChildNode,
            TranspilerState state,
            Function<Action, Expression> getRightSide
    ) {
        if (state.hasCurrentChildList()) {
            return this.addOnly(bodyChildNode, state, getRightSide.apply(Action.ADD));
        } else {
            return this.appendOnly(bodyChildNode, state, getRightSide.apply(Action.APPEND));
        }
    }

}
