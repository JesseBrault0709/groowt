package groowt.view.web.transpile;

import groowt.view.component.*;
import groowt.view.web.ast.node.*;
import groowt.view.web.lib.FragmentComponent;
import groowt.view.web.runtime.WebViewComponentChildCollector;
import groowt.view.web.transpile.TranspilerUtil.TranspilerState;
import groowt.view.web.transpile.util.GroovyUtil;
import groowt.view.web.transpile.util.GroovyUtil.ConvertResult;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static groowt.view.web.transpile.TranspilerUtil.lineAndColumn;
import static groowt.view.web.transpile.TranspilerUtil.makeStringLiteral;

public class DefaultComponentTranspiler implements ComponentTranspiler {

    private static final ClassNode VIEW_COMPONENT = ClassHelper.make(ViewComponent.class);
    private static final ClassNode CHILD_COLLECTOR = ClassHelper.make(WebViewComponentChildCollector.class);

    private static final ClassNode EXCEPTION = ClassHelper.make(Exception.class);
    private static final ClassNode COMPONENT_CREATE = ClassHelper.make(ComponentCreateException.class);

    private static final ClassNode NO_FACTORY_MISSING_EXCEPTION = ClassHelper.make(NoFactoryMissingException.class);

    private static final ClassNode MISSING_COMPONENT_EXCEPTION = ClassHelper.make(MissingComponentException.class);
    private static final ClassNode MISSING_CLASS_TYPE_EXCEPTION = ClassHelper.make(MissingClassTypeException.class);
    private static final ClassNode MISSING_STRING_TYPE_EXCEPTION = ClassHelper.make(MissingStringTypeException.class);
    private static final ClassNode MISSING_FRAGMENT_TYPE_EXCEPTION = ClassHelper.make(MissingFragmentTypeException.class);

    private static final String CREATE = "create";
    private static final String RESOLVE = "resolve";
    private static final String ADD = "add";
    private static final String APPEND = "append";
    private static final String FRAGMENT_FQN = FragmentComponent.class.getCanonicalName();

    private ValueNodeTranspiler valueNodeTranspiler;
    private BodyTranspiler bodyTranspiler;

    public void setValueNodeTranspiler(ValueNodeTranspiler valueNodeTranspiler) {
        this.valueNodeTranspiler = valueNodeTranspiler;
    }

    public void setBodyTranspiler(BodyTranspiler bodyTranspiler) {
        this.bodyTranspiler = bodyTranspiler;
    }

    // ViewComponent c0
    protected ExpressionStatement getComponentDeclaration(Variable component) {
        final var componentDeclaration = new DeclarationExpression(
                new VariableExpression(component),
                new Token(Types.ASSIGN, "=", -1, -1),
                EmptyExpression.INSTANCE
        );
        return new ExpressionStatement(componentDeclaration);
    }

    // 'ComponentName'
    protected ConstantExpression getComponentTypeNameExpression(ComponentNode componentNode) {
        final String componentTypeName = switch (componentNode) {
            case TypedComponentNode typedComponentNode -> switch (typedComponentNode.getArgs().getType()) {
                case ClassComponentTypeNode classComponentTypeNode -> classComponentTypeNode.getFullyQualifiedName();
                case StringComponentTypeNode stringComponentTypeNode -> stringComponentTypeNode.getIdentifier();
            };
            case FragmentComponentNode ignored -> FRAGMENT_FQN;
        };
        return makeStringLiteral(componentTypeName);
    }

    // context.resolve('ComponentName')
    protected MethodCallExpression getContextResolveExpr(ComponentNode componentNode, Variable componentContext) {
        final var args = new ArgumentListExpression();
        args.addExpression(this.getComponentTypeNameExpression(componentNode));
        return new MethodCallExpression(new VariableExpression(componentContext), RESOLVE, args);
    }

    // key: value
    protected MapEntryExpression getAttrExpression(
            AttrNode attrNode, TranspilerState state
    ) {
        final var keyExpr = makeStringLiteral(attrNode.getKeyNode().getKey());
        final Expression valueExpr = switch (attrNode) {
            case BooleanValueAttrNode ignored -> ConstantExpression.PRIM_TRUE;
            case KeyValueAttrNode keyValueAttrNode ->
                    this.valueNodeTranspiler.createExpression(keyValueAttrNode.getValueNode(), state);
        };
        return new MapEntryExpression(keyExpr, valueExpr);
    }

    // [key: value, ...]
    protected MapExpression getAttrMap(List<AttrNode> attributeNodes, TranspilerState state) {
        if (attributeNodes.isEmpty()) {
            throw new IllegalArgumentException("attributeNodes cannot be empty");
        }
        final var result = new MapExpression();
        attributeNodes.stream()
                .map(attrNode -> this.getAttrExpression(attrNode, state))
                .forEach(result::addMapEntryExpression);
        return result;
    }

    // arg0, arg1, arg2, etc
    protected List<Expression> getConstructorArgs(ComponentConstructorNode componentConstructorNode) {
        final ConvertResult convertResult = GroovyUtil.convert(componentConstructorNode.getGroovyCode()
                .getAsValidGroovyCode());
        final var blockStatement = convertResult.blockStatement();
        if (blockStatement == null) {
            throw new IllegalStateException("Did not expect blockStatement to be null");
        }
        final var statements = blockStatement.getStatements();
        if (statements.size() != 1) {
            throw new IllegalStateException("statements size is not 1");
        }
        final ExpressionStatement exprStmt = (ExpressionStatement) statements.getFirst();
        final ListExpression listExpr = (ListExpression) exprStmt.getExpression();
        return listExpr.getExpressions();
    }

    private void addLineAndColumn(Node sourceNode, ArgumentListExpression args) {
        final var lineAndColumn = lineAndColumn(sourceNode.getTokenRange().getStartPosition());
        args.addExpression(lineAndColumn.getV1());
        args.addExpression(lineAndColumn.getV2());
    }

    protected MethodCallExpression getOutCall(Node sourceNode, TranspilerState state, Expression toOutput) {
        final VariableExpression outVariableExpr = new VariableExpression(state.out());
        final ArgumentListExpression args = new ArgumentListExpression();
        args.addExpression(toOutput);
        switch (sourceNode) {
            case GStringBodyTextNode ignored -> this.addLineAndColumn(sourceNode, args);
            case ComponentNode ignored -> this.addLineAndColumn(sourceNode, args);
            default -> {
            }
        }
        return new MethodCallExpression(outVariableExpr, APPEND, args);
    }

    // { out << jString | gString | component }
    protected ClosureExpression getOutClosure(Node sourceNode, TranspilerState state, Expression toRender) {
        if (toRender instanceof VariableExpression variableExpression) {
            variableExpression.setClosureSharedVariable(true);
        }
        final Statement stmt = new ExpressionStatement(this.getOutCall(sourceNode, state, toRender));
        return new ClosureExpression(Parameter.EMPTY_ARRAY, stmt);
    }

    // c0_childCollector.add (jString | gString | component) { out << ... }
    protected Statement getChildCollectorAdd(
            Node sourceNode,
            TranspilerState state,
            Variable childCollector,
            Expression toAdd
    ) {
        final var childCollectorVariableExpr = new VariableExpression(childCollector);
        final ClosureExpression renderChild = this.getOutClosure(sourceNode, state, toAdd);
        final MethodCallExpression methodCall = new MethodCallExpression(
                childCollectorVariableExpr,
                ADD,
                new ArgumentListExpression(List.of(toAdd, renderChild))
        );
        return new ExpressionStatement(methodCall);
    }

    // { WebViewComponentChildCollector c0_childCollector -> ... }
    protected ClosureExpression getBodyClosure(BodyNode bodyNode, TranspilerState state, String componentVariableName) {
        final Parameter childCollectorParam = new Parameter(
                CHILD_COLLECTOR,
                componentVariableName + "_childCollector"
        );

        final var scope = state.pushScope();
        scope.putDeclaredVariable(childCollectorParam);
        final BlockStatement bodyStatements = this.bodyTranspiler.transpileBody(
                bodyNode,
                (sourceNode, expr) -> this.getChildCollectorAdd(sourceNode, state, childCollectorParam, expr),
                state
        );
        state.popScope();

        return new ClosureExpression(new Parameter[]{childCollectorParam}, bodyStatements);
    }

    // context.create(...) {...}
    protected MethodCallExpression getCreateExpression(
            ComponentNode componentNode, TranspilerState state, String componentVariableName
    ) {
        final var createArgs = new ArgumentListExpression();

        final var contextResolve = this.getContextResolveExpr(componentNode, state.context());
        createArgs.addExpression(contextResolve);

        if (componentNode instanceof TypedComponentNode typedComponentNode) {
            final List<AttrNode> attributeNodes = typedComponentNode.getArgs().getAttributes();
            if (!attributeNodes.isEmpty()) {
                createArgs.addExpression(this.getAttrMap(attributeNodes, state));
            }
            final ComponentConstructorNode constructorNode = typedComponentNode.getArgs().getConstructor();
            if (constructorNode != null) {
                this.getConstructorArgs(constructorNode).forEach(createArgs::addExpression);
            }
        }

        final @Nullable BodyNode bodyNode = componentNode.getBody();
        if (bodyNode != null) {
            createArgs.addExpression(this.getBodyClosure(bodyNode, state, componentVariableName));
        }

        return new MethodCallExpression(new VariableExpression(state.context()), CREATE, createArgs);
    }

    // c0 = context.create(context.resolve(''), [:], ...) {...}
    protected ExpressionStatement getCreateAssignStatement(
            ComponentNode componentNode, TranspilerState state, String componentVariableName
    ) {
        final var componentAssignLeft = new VariableExpression(state.getDeclaredVariable(componentVariableName));
        final var createExpr = this.getCreateExpression(componentNode, state, componentVariableName);
        final var componentAssignExpr = new BinaryExpression(
                componentAssignLeft,
                new Token(Types.ASSIGN, "=", -1, -1),
                createExpr
        );
        return new ExpressionStatement(componentAssignExpr);
    }

    // catch (NoFactoryMissingException c0nfme) {
    //     throw new MissingClassComponentException(this, 'ComponentType', c0nfme)
    // }
    protected CatchStatement getNoMissingFactoryExceptionCatch(
            ComponentNode componentNode, String componentVariableName
    ) {
        final String exceptionName = componentVariableName + "nfme";
        final Parameter fmeParam = new Parameter(NO_FACTORY_MISSING_EXCEPTION, exceptionName);
        final VariableExpression fmeVar = new VariableExpression(exceptionName);

        final var lineAndColumn = lineAndColumn(componentNode.getTokenRange().getStartPosition());
        final ConstantExpression line = lineAndColumn.getV1();
        final ConstantExpression column = lineAndColumn.getV2();

        final ConstructorCallExpression mcceConstructorExpr = switch (componentNode) {
            case TypedComponentNode typedComponentNode -> switch (typedComponentNode.getArgs().getType()) {
                case StringComponentTypeNode stringComponentTypeNode ->
                        new ConstructorCallExpression(MISSING_STRING_TYPE_EXCEPTION, new ArgumentListExpression(List.of(
                                VariableExpression.THIS_EXPRESSION,
                                makeStringLiteral(stringComponentTypeNode.getIdentifier()),
                                line,
                                column,
                                fmeVar
                        )));
                case ClassComponentTypeNode classComponentTypeNode ->
                        new ConstructorCallExpression(MISSING_CLASS_TYPE_EXCEPTION, new ArgumentListExpression(List.of(
                                VariableExpression.THIS_EXPRESSION,
                                makeStringLiteral(classComponentTypeNode.getIdentifier()),
                                line,
                                column,
                                fmeVar
                        )));
            };
            case FragmentComponentNode ignored -> new ConstructorCallExpression(
                    MISSING_FRAGMENT_TYPE_EXCEPTION,
                    new ArgumentListExpression(List.of(VariableExpression.THIS_EXPRESSION, line, column, fmeVar))
            );
        };
        final Statement throwMcceStmt = new ThrowStatement(mcceConstructorExpr);
        return new CatchStatement(fmeParam, throwMcceStmt);
    }

    // catch (MissingComponentException c0mce) { throw c0mce }
    protected CatchStatement getMissingComponentExceptionCatch(String componentVariableName) {
        final String exceptionName = componentVariableName + "mce";
        final Parameter exceptionParam = new Parameter(MISSING_COMPONENT_EXCEPTION, exceptionName);
        final VariableExpression mceVar = new VariableExpression(exceptionName);
        final Statement throwMceStmt = new ThrowStatement(mceVar);
        return new CatchStatement(exceptionParam, throwMceStmt);
    }

    // catch (Exception c0ce) { throw new ComponentCreateException(c0ce) }
    protected CatchStatement getGeneralCreateExceptionCatch(ComponentNode componentNode, String componentVariableName) {
        final String exceptionName = componentVariableName + "ce";
        final Parameter exceptionParam = new Parameter(EXCEPTION, exceptionName);
        final VariableExpression exceptionVar = new VariableExpression(exceptionName);

        final var lineAndColumn = lineAndColumn(componentNode.getTokenRange().getStartPosition());

        final ConstructorCallExpression cce = new ConstructorCallExpression(
                COMPONENT_CREATE,
                new ArgumentListExpression(List.of(
                        VariableExpression.THIS_EXPRESSION,
                        lineAndColumn.getV1(),
                        lineAndColumn.getV2(),
                        exceptionVar
                ))
        );
        final Statement throwCcStmt = new ThrowStatement(cce);
        return new CatchStatement(exceptionParam, throwCcStmt);
    }

    protected List<CatchStatement> getCreateCatches(ComponentNode componentNode, String componentVariableName) {
        final List<CatchStatement> catches = new ArrayList<>();
        catches.add(this.getNoMissingFactoryExceptionCatch(componentNode, componentVariableName));
        catches.add(this.getMissingComponentExceptionCatch(componentVariableName));
        catches.add(this.getGeneralCreateExceptionCatch(componentNode, componentVariableName));
        return catches;
    }

    protected Statement createSetContext(TranspilerState state, Variable component) {
        final VariableExpression componentExpr = new VariableExpression(component);
        final VariableExpression contextExpr = new VariableExpression(state.context());
        final var args = new ArgumentListExpression(contextExpr);
        final var setContext = new MethodCallExpression(componentExpr, "setContext", args);
        return new ExpressionStatement(setContext);
    }

    protected Statement createComponentOutCall(
            ComponentNode componentNode,
            TranspilerState state,
            Variable component
    ) {
        // out << c0
        final VariableExpression toOutput = new VariableExpression(component);
        final Expression outCallExpr = this.getOutCall(componentNode, state, toOutput);
        return new ExpressionStatement(outCallExpr);
    }

    protected String getComponentVariableName(int componentNumber) {
        return "c" + componentNumber;
    }

    @Override
    public BlockStatement createComponentStatements(
            ComponentNode componentNode,
            TranspilerState state
    ) {
        final var componentVariableName = this.getComponentVariableName(state.newComponentNumber());
        final Variable component = new VariableExpression(componentVariableName, VIEW_COMPONENT);

        final BlockStatement result = new BlockStatement();
        final VariableScope scope = state.pushScope();
        result.setVariableScope(scope);
        scope.putDeclaredVariable(component);

        // ViewComponent c0;
        result.addStatement(this.getComponentDeclaration(component));

        // try { context.create(...) } catch { ... }
        final var tryCreateStatement = new TryCatchStatement(this.getCreateAssignStatement(
                componentNode,
                state,
                componentVariableName
        ), EmptyStatement.INSTANCE);
        this.getCreateCatches(componentNode, componentVariableName).forEach(tryCreateStatement::addCatch);
        result.addStatement(tryCreateStatement);

        // component.setContext(context)
        result.addStatement(this.createSetContext(state, component));

        // out << component
        result.addStatement(this.createComponentOutCall(componentNode, state, component));

        state.popScope();

        return result;
    }

}
