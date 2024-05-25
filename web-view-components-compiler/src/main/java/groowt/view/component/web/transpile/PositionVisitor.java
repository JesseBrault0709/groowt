package groowt.view.component.web.transpile;

import groowt.view.component.web.ast.node.Node;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.control.SourceUnit;

// TODO: create a utility walker-visitor class to make this much simpler
public class PositionVisitor extends ClassCodeVisitorSupport {

    private final PositionSetter positionSetter;
    private final Node container;

    public PositionVisitor(PositionSetter positionSetter, Node container) {
        this.positionSetter = positionSetter;
        this.container = container;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitClass(ClassNode node) {
        super.visitClass(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        super.visitAnnotations(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    protected void visitAnnotation(AnnotationNode node) {
        super.visitAnnotation(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitPackage(PackageNode node) {
        super.visitPackage(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitImports(ModuleNode node) {
        super.visitImports(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        super.visitConstructor(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitMethod(MethodNode node) {
        super.visitMethod(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        super.visitConstructorOrMethod(node, isConstructor);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitField(FieldNode node) {
        super.visitField(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitProperty(PropertyNode node) {
        super.visitProperty(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    protected void visitObjectInitializerStatements(ClassNode node) {
        super.visitObjectInitializerStatements(node);
        this.positionSetter.setPositionOffsetInContainer(node, container);
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        super.visitDeclarationExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        super.visitAssertStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitBlockStatement(BlockStatement statement) {
        super.visitBlockStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
        super.visitBreakStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        super.visitCaseStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        super.visitCatchStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
        super.visitContinueStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement statement) {
        super.visitDoWhileLoop(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        super.visitExpressionStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitForLoop(ForStatement statement) {
        super.visitForLoop(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitIfElse(IfStatement statement) {
        super.visitIfElse(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        super.visitReturnStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        super.visitSwitch(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        super.visitSynchronizedStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        super.visitThrowStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        super.visitTryCatchFinally(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitWhileLoop(WhileStatement statement) {
        super.visitWhileLoop(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitEmptyStatement(EmptyStatement statement) {
        super.visitEmptyStatement(statement);
        this.positionSetter.setPositionOffsetInContainer(statement, container);
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        super.visitMethodCallExpression(call);
        this.positionSetter.setPositionOffsetInContainer(call, container);
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        super.visitStaticMethodCallExpression(call);
        this.positionSetter.setPositionOffsetInContainer(call, container);
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call);
        this.positionSetter.setPositionOffsetInContainer(call, container);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        super.visitBinaryExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        super.visitTernaryExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        super.visitShortTernaryExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        super.visitPostfixExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        super.visitPrefixExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        super.visitBooleanExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
        super.visitNotExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        super.visitClosureExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitLambdaExpression(LambdaExpression expression) {
        super.visitLambdaExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitTupleExpression(TupleExpression expression) {
        super.visitTupleExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitListExpression(ListExpression expression) {
        super.visitListExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        super.visitArrayExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        super.visitMapExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        super.visitMapEntryExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        super.visitRangeExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
        super.visitSpreadExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        super.visitSpreadMapExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        super.visitMethodPointerExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitMethodReferenceExpression(MethodReferenceExpression expression) {
        super.visitMethodReferenceExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        super.visitUnaryMinusExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        super.visitUnaryPlusExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        super.visitBitwiseNegationExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
        super.visitConstantExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
        super.visitClassExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        super.visitVariableExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        super.visitPropertyExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
        super.visitAttributeExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
        super.visitFieldExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
        super.visitGStringExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression expression) {
        super.visitArgumentlistExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression expression) {
        super.visitClosureListExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression expression) {
        super.visitBytecodeExpression(expression);
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

    @Override
    public void visit(Statement statement) {
        super.visit(statement);
    }

    @Override
    public void visitEmptyExpression(EmptyExpression expression) {
        this.positionSetter.setPositionOffsetInContainer(expression, container);
    }

}
