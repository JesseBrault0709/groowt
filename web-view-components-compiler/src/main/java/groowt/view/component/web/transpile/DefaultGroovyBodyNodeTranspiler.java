package groowt.view.component.web.transpile;

import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.*;
import groowt.view.component.web.transpile.groovy.GroovyUtil;
import jakarta.inject.Inject;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import java.util.List;

public class DefaultGroovyBodyNodeTranspiler implements GroovyBodyNodeTranspiler {

    private final PositionSetter positionSetter;
    private final LeftShiftFactory leftShiftFactory;

    @Inject
    public DefaultGroovyBodyNodeTranspiler(PositionSetter positionSetter, LeftShiftFactory leftShiftFactory) {
        this.positionSetter = positionSetter;
        this.leftShiftFactory = leftShiftFactory;
    }

    protected ClosureExpression convertToClosure(Node node, String source) {
        final GroovyUtil.ConvertResult convertResult = GroovyUtil.convert(
                "def cl = {" + source + "}"
        );
        final BlockStatement convertBlock = convertResult.blockStatement();

        if (convertBlock == null) {
            throw new WebViewComponentBugError("Did not expect convertBlock to be null.");
        }
        if (convertBlock.isEmpty()) {
            throw new WebViewComponentBugError("Did not expect convertBlock to be empty.");
        }

        final ExpressionStatement clStmt = (ExpressionStatement) convertBlock.getStatements().getFirst();
        final BinaryExpression clAssign = (BinaryExpression) clStmt.getExpression();
        final ClosureExpression cl = (ClosureExpression) clAssign.getRightExpression();

        final PositionVisitor positionVisitor = new PositionVisitor(
                this.positionSetter.withOffset(0, -10), node
        );
        cl.visit(positionVisitor);

        return cl;
    }

    protected Statement handleEqualsScriptlet(EqualsScriptletNode equalsScriptletNode, TranspilerState state) {
        final ClosureExpression cl = this.convertToClosure(equalsScriptletNode, equalsScriptletNode.getGroovyCode());
        final MethodCallExpression callExpr;
        if (cl.getParameters() == null) {
            callExpr = new MethodCallExpression(cl, "call", EmptyExpression.INSTANCE);
        } else {
            final ArgumentListExpression argsList = new ArgumentListExpression(
                    List.of(state.hasCurrentChildList() ? state.getCurrentChildList() : state.getWriter())
            );
            callExpr = new MethodCallExpression(cl, "call", argsList);
        }
        return this.leftShiftFactory.create(callExpr);
    }

    protected Statement handlePlainScriptlet(PlainScriptletNode plainScriptletNode, TranspilerState state) {
        final ClosureExpression cl = this.convertToClosure(plainScriptletNode, plainScriptletNode.getGroovyCode());
        final MethodCallExpression callExpr;
        if (cl.getParameters() == null) {
            callExpr = new MethodCallExpression(cl, "call", EmptyExpression.INSTANCE);
        } else {
            final ArgumentListExpression argsList = new ArgumentListExpression(
                    List.of(state.hasCurrentChildList() ? state.getCurrentChildList() : state.getWriter())
            );
            callExpr = new MethodCallExpression(cl, "call", argsList);
        }

        return new ExpressionStatement(callExpr);
    }

    protected Statement handleDollarScriptlet(DollarScriptletNode dollarScriptletNode) {
        final ClosureExpression cl = this.convertToClosure(dollarScriptletNode, dollarScriptletNode.getGroovyCode());
        final Expression toLeftShift;
        if (cl.getParameters() == null) {
            toLeftShift = cl;
        } else {
            final Statement stmt = cl.getCode();
            if (stmt instanceof ExpressionStatement exprStmt) {
                toLeftShift = exprStmt.getExpression();
            } else {
                toLeftShift = cl;
            }
        }
        return this.leftShiftFactory.create(toLeftShift);
    }

    protected Statement handleDollarReference(DollarReferenceNode dollarReferenceNode) {
        VariableExpression root = null;
        PropertyExpression propertyExpr = null;
        for (final String part : dollarReferenceNode.getParts()) {
            if (root == null) {
                root = new VariableExpression(part);
            } else if (propertyExpr == null) {
                propertyExpr = new PropertyExpression(root, part);
            } else {
                propertyExpr = new PropertyExpression(propertyExpr, part);
            }
        }
        final var positionVisitor = new PositionVisitor(this.positionSetter, dollarReferenceNode);
        if (propertyExpr != null) {
            propertyExpr.visit(positionVisitor);
            return this.leftShiftFactory.create(propertyExpr);
        } else if (root != null) {
            root.visit(positionVisitor);
            return this.leftShiftFactory.create(root);
        } else {
            throw new WebViewComponentBugError("Did not expect root to be null.");
        }
    }

    @Override
    public Statement createGroovyBodyNodeStatements(GroovyBodyNode groovyBodyNode, TranspilerState state) {
        return switch (groovyBodyNode) {
            case EqualsScriptletNode equalsScriptletNode -> this.handleEqualsScriptlet(equalsScriptletNode, state);
            case PlainScriptletNode plainScriptletNode -> this.handlePlainScriptlet(plainScriptletNode, state);
            case DollarScriptletNode dollarScriptletNode -> this.handleDollarScriptlet(dollarScriptletNode);
            case DollarReferenceNode dollarReferenceNode -> this.handleDollarReference(dollarReferenceNode);
            default -> throw new WebViewComponentBugError(new UnsupportedOperationException(
                    "GroovyBodyNode of type " + groovyBodyNode.getClass().getName() + " is not supported."
            ));
        };
    }

}
