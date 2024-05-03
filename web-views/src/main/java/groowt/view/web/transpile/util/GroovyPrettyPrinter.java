package groowt.view.web.transpile.util;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.control.SourceUnit;

import java.util.List;

public final class GroovyPrettyPrinter extends ClassCodeVisitorSupport {

    public interface OnEachNode {

        /**
         * @param typeName      the type name of the node being visited
         * @param astNode       the node
         * @param stringBuilder target for your message
         * @implNote Do not append a newline at the end of your message, as that is done later by the
         * {@link GroovyPrettyPrinter}.
         */
        void accept(StringBuilder stringBuilder, String typeName, ASTNode astNode);

        /**
         * Called when a visitMethod exposes a list of nodes. See {@link #accept(StringBuilder, String, ASTNode)}.
         */
        void accept(StringBuilder stringBuilder, String visitMethodName, List<? extends ASTNode> nodeList);

    }

    public static final OnEachNode SIMPLE = new OnEachNode() {

        @Override
        public void accept(StringBuilder sb, String typeName, ASTNode astNode) {
            sb.append(typeName).append(GroovyUtilKt.formatGroovyPosition(astNode));
        }

        @Override
        public void accept(StringBuilder sb, String visitMethodName, List<? extends ASTNode> nodeList) {
            sb.append(visitMethodName);
        }

    };
    
    private static final String INDENT = "  ";
    private final StringBuilder sb = new StringBuilder();
    private final OnEachNode onEachNode;
    private int indentTimes;

    public GroovyPrettyPrinter(OnEachNode onEachNode) {
        this.onEachNode = onEachNode;
    }

    public GroovyPrettyPrinter() {
        this.onEachNode = SIMPLE;
    }

    public String getResult() {
        return this.sb.toString();
    }

    private void before(String name, ASTNode astNode) {
        if (astNode != null) {
            this.sb.repeat(INDENT, this.indentTimes);
            this.onEachNode.accept(this.sb, name, astNode);
            this.sb.append("\n");
        }
        this.indentTimes++;
    }

    private void before(String name, List<? extends ASTNode> nodes) {
        this.sb.repeat(INDENT, this.indentTimes);
        this.onEachNode.accept(this.sb, name, nodes);
        this.sb.append("\n");
        this.indentTimes++;
    }

    private void before(String name) {
        this.sb.repeat(INDENT, this.indentTimes);
        this.sb.append("\n");
        this.indentTimes++;
    }

    private void after() {
        this.indentTimes--;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null;
    }

    @Override
    public void visitClass(ClassNode node) {
        this.before("Class", node);
        super.visitClass(node);
        this.after();
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        this.before("Annotations", node.getAnnotations());
        super.visitAnnotations(node);
        this.after();
    }

    @Override
    protected void visitAnnotation(AnnotationNode node) {
        this.before("Annotation", node);
        super.visitAnnotation(node);
        this.after();
    }

    @Override
    public void visitPackage(PackageNode node) {
        this.before("Package", node);
        super.visitPackage(node);
        this.after();
    }

    @Override
    public void visitImports(ModuleNode node) {
        this.before("Imports", node.getImports());
        node.getImports().forEach(this::visitImport);
        node.getStarImports().forEach(this::visitImport);
        node.getStaticImports().forEach(this::visitImport);
        node.getStaticStarImports().forEach(this::visitImport);
        this.after();
    }

    public void visitImport(ImportNode importNode) {
        this.before("Import", importNode);
        this.visitAnnotations(importNode.getAnnotations());
        this.after();
    }

    public void visitImport(String alias, ImportNode importNode) {
        this.before("Import", importNode);
        this.visitAnnotations(importNode.getAnnotations());
        this.after();
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        this.before("Constructor", node);
        super.visitConstructor(node);
        this.after();
    }

    @Override
    public void visitMethod(MethodNode node) {
        this.before("Method", node);
        super.visitMethod(node);
        this.after();
    }

    @Override
    public void visitField(FieldNode node) {
        this.before("Field", node);
        super.visitField(node);
        this.after();
    }

    @Override
    public void visitProperty(PropertyNode node) {
        this.before("Property", node);
        super.visitProperty(node);
        this.after();
    }

    @Override
    protected void visitObjectInitializerStatements(ClassNode node) {
        this.before("ObjectInitializerStatements", node.getObjectInitializerStatements());
        super.visitObjectInitializerStatements(node);
        this.after();
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        this.before("BlockStatement", block);
        super.visitBlockStatement(block);
        this.after();
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        this.before("ForLoop", forLoop);
        super.visitForLoop(forLoop);
        this.after();
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        this.before("WhileLoop", loop);
        super.visitWhileLoop(loop);
        this.after();
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        this.before("DoWhileLoop", loop);
        super.visitDoWhileLoop(loop);
        this.after();
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        this.before("IfElse", ifElse);
        super.visitIfElse(ifElse);
        this.after();
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        this.before("ExpressionStatement", statement);
        super.visitExpressionStatement(statement);
        this.after();
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        this.before("ReturnStatement", statement);
        super.visitReturnStatement(statement);
        this.after();
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        this.before("AssertStatement", statement);
        super.visitAssertStatement(statement);
        this.after();
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        this.before("TryCatchFinally", statement);
        super.visitTryCatchFinally(statement);
        this.after();
    }

    @Override
    public void visitEmptyStatement(EmptyStatement statement) {
        this.before("EmptyStatement", statement);
        super.visitEmptyStatement(statement);
        this.after();
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        this.before("Switch", statement);
        super.visitSwitch(statement);
        this.after();
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        this.before("CaseStatement", statement);
        super.visitCaseStatement(statement);
        this.after();
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
        this.before("BreakStatement", statement);
        super.visitBreakStatement(statement);
        this.after();
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
        this.before("ContinueStatement", statement);
        super.visitContinueStatement(statement);
        this.after();
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        this.before("SynchronizedStatement", statement);
        super.visitSynchronizedStatement(statement);
        this.after();
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        this.before("ThrowStatement", statement);
        super.visitThrowStatement(statement);
        this.after();
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        this.before("MethodCallExpression", call);
        super.visitMethodCallExpression(call);
        this.after();
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        this.before("StaticMethodCallExpression", call);
        super.visitStaticMethodCallExpression(call);
        this.after();
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        this.before("ConstructorCallExpression", call);
        super.visitConstructorCallExpression(call);
        this.after();
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        this.before("BinaryExpression", expression);
        super.visitBinaryExpression(expression);
        this.after();
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        this.before("TernaryExpression", expression);
        super.visitTernaryExpression(expression);
        this.after();
    }

    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        this.before("ShortTernaryExpression", expression);
        super.visitShortTernaryExpression(expression);
        this.after();
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        this.before("PostfixExpression", expression);
        super.visitPostfixExpression(expression);
        this.after();
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        this.before("PrefixExpression", expression);
        super.visitPrefixExpression(expression);
        this.after();
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        this.before("BooleanExpression", expression);
        super.visitBooleanExpression(expression);
        this.after();
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
        this.before("NotExpression", expression);
        super.visitNotExpression(expression);
        this.after();
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        this.before("ClosureExpression", expression);
        super.visitClosureExpression(expression);
        this.after();
    }

    @Override
    public void visitLambdaExpression(LambdaExpression expression) {
        this.before("LambdaExpression", expression);
        super.visitLambdaExpression(expression);
        this.after();
    }

    @Override
    public void visitTupleExpression(TupleExpression expression) {
        this.before("TupleExpression", expression);
        super.visitTupleExpression(expression);
        this.after();
    }

    @Override
    public void visitListExpression(ListExpression expression) {
        this.before("ListExpression", expression);
        super.visitListExpression(expression);
        this.after();
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        this.before("ArrayExpression", expression);
        super.visitArrayExpression(expression);
        this.after();
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        this.before("MapExpression", expression);
        super.visitMapExpression(expression);
        this.after();
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        this.before("MapEntryExpression", expression);
        super.visitMapEntryExpression(expression);
        this.after();
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        this.before("RangeExpression", expression);
        super.visitRangeExpression(expression);
        this.after();
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
        this.before("SpreadExpression", expression);
        super.visitSpreadExpression(expression);
        this.after();
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        this.before("SpreadMapExpression", expression);
        super.visitSpreadMapExpression(expression);
        this.after();
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        this.before("MethodPointerExpression", expression);
        super.visitMethodPointerExpression(expression);
        this.after();
    }

    @Override
    public void visitMethodReferenceExpression(MethodReferenceExpression expression) {
        this.before("MethodReferenceExpression", expression);
        super.visitMethodReferenceExpression(expression);
        this.after();
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        this.before("UnaryMinusExpression", expression);
        super.visitUnaryMinusExpression(expression);
        this.after();
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        this.before("UnaryPlusExpression", expression);
        super.visitUnaryPlusExpression(expression);
        this.after();
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        this.before("BitwiseNegationExpression", expression);
        super.visitBitwiseNegationExpression(expression);
        this.after();
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        this.before("CastExpression", expression);
        super.visitCastExpression(expression);
        this.after();
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
        this.before("ConstantExpression", expression);
        super.visitConstantExpression(expression);
        this.after();
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
        this.before("ClassExpression", expression);
        super.visitClassExpression(expression);
        this.after();
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        this.before("VariableExpression", expression);
        super.visitVariableExpression(expression);
        this.after();
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        this.before("DeclarationExpression", expression);
        super.visitDeclarationExpression(expression);
        this.after();
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        this.before("PropertyExpression", expression);
        super.visitPropertyExpression(expression);
        this.after();
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
        this.before("AttributeExpression", expression);
        super.visitAttributeExpression(expression);
        this.after();
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
        this.before("FieldExpression", expression);
        super.visitFieldExpression(expression);
        this.after();
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
        this.before("GStringExpression", expression);
        super.visitGStringExpression(expression);
        this.after();
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        this.before("CatchStatement", statement);
        super.visitCatchStatement(statement);
        this.after();
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression expression) {
        this.before("ArgumentlistExpression", expression);
        super.visitArgumentlistExpression(expression);
        this.after();
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression expression) {
        this.before("ClosureListExpression", expression);
        super.visitClosureListExpression(expression);
        this.after();
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression expression) {
        this.before("BytecodeExpression", expression);
        super.visitBytecodeExpression(expression);
        this.after();
    }

}
