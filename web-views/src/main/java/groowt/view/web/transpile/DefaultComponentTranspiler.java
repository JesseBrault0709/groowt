package groowt.view.web.transpile;

import groowt.view.component.context.ComponentResolveException;
import groowt.view.component.runtime.ComponentCreateException;
import groowt.view.component.runtime.RenderContext;
import groowt.view.web.WebViewComponentBugError;
import groowt.view.web.ast.node.*;
import groowt.view.web.runtime.WebViewComponentChildCollector;
import groowt.view.web.runtime.WebViewComponentChildCollectorClosure;
import groowt.view.web.transpile.resolve.ComponentClassNodeResolver;
import groowt.view.web.transpile.util.GroovyUtil;
import groowt.view.web.transpile.util.GroovyUtil.ConvertResult;
import groowt.view.web.util.Provider;
import groowt.view.web.util.SourcePosition;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static groowt.view.web.transpile.TranspilerUtil.*;

public class DefaultComponentTranspiler implements ComponentTranspiler {

    private static final ClassNode CHILD_COLLECTOR_TYPE = ClassHelper.make(WebViewComponentChildCollector.class);
    private static final ClassNode FRAGMENT_TYPE = ClassHelper.make(GROOWT_VIEW_WEB + ".lib.Fragment");
    private static final ClassNode RESOLVED_TYPE = ClassHelper.make(RenderContext.Resolved.class);
    private static final ClassNode CHILD_COLLECTOR_CLOSURE_TYPE =
            ClassHelper.make(WebViewComponentChildCollectorClosure.class);
    private static final ClassNode COMPONENT_RESOLVE_EXCEPTION_TYPE = ClassHelper.make(ComponentResolveException.class);
    private static final ClassNode COMPONENT_CREATE_EXCEPTION_TYPE = ClassHelper.make(ComponentCreateException.class);

    private static final Pattern isFqn = Pattern.compile("^(\\p{Ll}.+\\.)+\\p{Lu}.+$");
    private static final Pattern isWithPackage = Pattern.compile("^\\p{Ll}.+\\.");

    private final Provider<AppendOrAddStatementFactory> appendOrAddStatementFactoryProvider;
    private final Provider<ComponentClassNodeResolver> componentClassNodeResolverProvider;
    private final Provider<ValueNodeTranspiler> valueNodeTranspilerProvider;
    private final Provider<BodyTranspiler> bodyTranspilerProvider;

    public DefaultComponentTranspiler(
            Provider<AppendOrAddStatementFactory> appendOrAddStatementFactoryProvider,
            Provider<ComponentClassNodeResolver> componentClassNodeResolverProvider,
            Provider<ValueNodeTranspiler> valueNodeTranspilerProvider,
            Provider<BodyTranspiler> bodyTranspilerProvider
    ) {
        this.appendOrAddStatementFactoryProvider = appendOrAddStatementFactoryProvider;
        this.componentClassNodeResolverProvider = componentClassNodeResolverProvider;
        this.valueNodeTranspilerProvider = valueNodeTranspilerProvider;
        this.bodyTranspilerProvider = bodyTranspilerProvider;
    }

    protected ValueNodeTranspiler getValueNodeTranspiler() {
        return this.valueNodeTranspilerProvider.get();
    }

    protected BodyTranspiler getBodyTranspiler() {
        return this.bodyTranspilerProvider.get();
    }

    protected AppendOrAddStatementFactory getAppendOrAddStatementFactory() {
        return this.appendOrAddStatementFactoryProvider.get();
    }

    protected ComponentClassNodeResolver getComponentClassNodeResolver() {
        return this.componentClassNodeResolverProvider.get();
    }

    /* UTIL */

    private void addLineAndColumn(Node sourceNode, ArgumentListExpression args) {
        final var lineAndColumn = lineAndColumn(sourceNode.getTokenRange().getStartPosition());
        args.addExpression(lineAndColumn.getV1());
        args.addExpression(lineAndColumn.getV2());
    }

    protected String getComponentName(int componentNumber) {
        return "c" + componentNumber;
    }

    /* RESOLVED DECLARATION */

    // RenderContext.Resolved c0Resolved
    protected Statement getResolvedDeclaration(TranspilerState state) {
        final ClassNode resolvedType = RESOLVED_TYPE.getPlainNodeReference();
        resolvedType.setGenericsTypes(
                new GenericsType[] { new GenericsType(WEB_VIEW_COMPONENT_TYPE) }
        );
        final var resolvedVariable = new VariableExpression(
                this.getComponentName(state.newComponentNumber()) + "Resolved",
                    resolvedType
        );
        state.pushResolved(resolvedVariable);
        final var declarationExpr = new DeclarationExpression(
                resolvedVariable,
                getAssignToken(),
                EmptyExpression.INSTANCE
        );
        return new ExpressionStatement(declarationExpr);
    }

    /* RESOLVE */

    protected List<Expression> getArgsAsList(
            TypedComponentNode componentNode,
            TranspilerState state
    ) {
        return switch (componentNode.getArgs().getType()) {
            case ClassComponentTypeNode classComponentTypeNode -> {
                final String identifier = classComponentTypeNode.getIdentifier();
                final ConstantExpression alias = getStringLiteral(identifier);
                final var matcher = isFqn.matcher(identifier);
                if (matcher.matches()) {
                    final ClassNode classNode = ClassHelper.make(identifier);
                    final ClassExpression classExpression = new ClassExpression(classNode);
                    yield List.of(alias, classExpression);
                } else {
                    // we need to resolve it
                    final var isWithPackageMatcher = isWithPackage.matcher(identifier);
                    if (isWithPackageMatcher.matches()) {
                        final var resolveResult = this.getComponentClassNodeResolver().getClassForFqn(identifier);
                        if (resolveResult.isLeft()) {
                            final var error = resolveResult.getLeft();
                            error.setNode(componentNode.getArgs().getType());
                            state.addError(error);
                            yield List.of();
                        } else {
                            final ClassNode classNode = resolveResult.getRight();
                            final ClassExpression classExpression = new ClassExpression(classNode); // TODO: pos
                            yield List.of(alias, classExpression);
                        }
                    } else {
                        final var resolveResult =
                                this.getComponentClassNodeResolver().getClassForNameWithoutPackage(identifier);
                        if (resolveResult.isLeft()) {
                            final var error = resolveResult.getLeft();
                            error.setNode(componentNode.getArgs().getType());
                            state.addError(error);
                            yield List.of();
                        } else {
                            final ClassNode classNode = resolveResult.getRight();
                            final ClassExpression classExpression = new ClassExpression(classNode); // TODO: pos
                            yield List.of(alias, classExpression);
                        }
                    }
                }
            }
            case StringComponentTypeNode stringComponentTypeNode -> {
                final String identifier = stringComponentTypeNode.getIdentifier();
                final ConstantExpression typeName = getStringLiteral(identifier);
                yield List.of(typeName);
            }
        };
    }

    // 'h1' | 'MyComponent', MyComponent(.class)
    protected ArgumentListExpression getResolveArgs(TypedComponentNode componentNode, TranspilerState state) {
        final List<Expression> args = this.getArgsAsList(componentNode, state);
        final ArgumentListExpression argsListExpr = new ArgumentListExpression();
        args.forEach(argsListExpr::addExpression);
        return argsListExpr;
    }

    // context.resolve('h1' | 'MyComponent', MyComponent.class)
    protected MethodCallExpression getContextResolveExpr(
            TypedComponentNode componentNode,
            TranspilerState state
    ) {
        return new MethodCallExpression(
                new VariableExpression(state.getRenderContext()),
                "resolve",
                this.getResolveArgs(componentNode, state)
        );
    }

    // context.resolve('h1' | 'MyComponent', MyComponent.class)
    protected ExpressionStatement getContextResolveStmt(
            TypedComponentNode componentNode,
            TranspilerState state
    ) {
        final BinaryExpression assignment = new BinaryExpression(
                new VariableExpression(state.getCurrentResolved()),
                getAssignToken(),
                this.getContextResolveExpr(componentNode, state)
        );
        return new ExpressionStatement(assignment);
    }

    /* RESOLVE CATCH */

    protected CatchStatement getResolveCatch(TypedComponentNode componentNode) {
        final Parameter exceptionParameter = new Parameter(
                COMPONENT_RESOLVE_EXCEPTION_TYPE,
                "componentResolveException"
        );
        final VariableExpression exceptionVariable = new VariableExpression(exceptionParameter);

        final VariableScope variableScope = new VariableScope();
        variableScope.putDeclaredVariable(exceptionParameter);

        final BinaryExpression setTemplateExpression = new BinaryExpression(
                new PropertyExpression(exceptionVariable, new ConstantExpression("template")),
                getAssignToken(),
                VariableExpression.THIS_EXPRESSION
        );
        final Statement setTemplateStatement = new ExpressionStatement(setTemplateExpression);

        final SourcePosition position = componentNode.getTokenRange().getStartPosition();

        final BinaryExpression setLineExpression = new BinaryExpression(
                new PropertyExpression(exceptionVariable, new ConstantExpression("line")),
                getAssignToken(),
                new ConstantExpression(position.line())
        );
        final Statement setLineStatement = new ExpressionStatement(setLineExpression);

        final BinaryExpression setColumnExpression = new BinaryExpression(
                new PropertyExpression(exceptionVariable, new ConstantExpression("column")),
                getAssignToken(),
                new ConstantExpression(position.column())
        );
        final Statement setColumnStatement = new ExpressionStatement(setColumnExpression);

        final Statement throwStatement = new ThrowStatement(exceptionVariable);

        final List<Statement> statements = new ArrayList<>();
        statements.add(setTemplateStatement);
        statements.add(setLineStatement);
        statements.add(setColumnStatement);
        statements.add(throwStatement);

        final BlockStatement block = new BlockStatement(statements, variableScope);

        return new CatchStatement(exceptionParameter, block);
    }

    /* RESOLVE BLOCK */

    protected List<Statement> getResolveStatements(
            TypedComponentNode componentNode,
            TranspilerState state
    ) {
        final Statement declaration = this.getResolvedDeclaration(state);
        final Statement resolveStatement = this.getContextResolveStmt(componentNode, state);

        final TryCatchStatement resolveTryCatch = new TryCatchStatement(resolveStatement, EmptyStatement.INSTANCE);
        resolveTryCatch.addCatch(this.getResolveCatch(componentNode));
        return List.of(declaration, resolveTryCatch);
    }

    /* TYPED COMPONENT DECLARATION */

    // ViewComponent c0
    protected Statement getTypedComponentDeclaration(TranspilerState state) {
        final VariableExpression componentVariable = new VariableExpression(
                this.getComponentName(state.getCurrentComponentNumber()), WEB_VIEW_COMPONENT_TYPE
        );
        state.pushComponent(componentVariable);
        state.getCurrentScope().putDeclaredVariable(componentVariable);

        final Expression declarationExpr = new DeclarationExpression(
                componentVariable,
                getAssignToken(),
                EmptyExpression.INSTANCE
        );
        return new ExpressionStatement(declarationExpr);
    }

    /* TYPED COMPONENT CREATE: attributes map */

    // key: value
    protected MapEntryExpression getAttrExpression(AttrNode attrNode, TranspilerState state) {
        final ConstantExpression keyExpr = getStringLiteral(attrNode.getKeyNode().getKey()); // TODO: pos
        final Expression valueExpr = switch (attrNode) {
            case BooleanValueAttrNode ignored -> ConstantExpression.PRIM_TRUE;
            case KeyValueAttrNode keyValueAttrNode ->
                    this.getValueNodeTranspiler().createExpression(keyValueAttrNode.getValueNode(), state);
        };
        return new MapEntryExpression(keyExpr, valueExpr);
    }

    // [key: value, ...]
    protected MapExpression getAttrMap(List<AttrNode> attributeNodes, TranspilerState state) {
        if (attributeNodes.isEmpty()) {
            throw new WebViewComponentBugError(new IllegalArgumentException("attributeNodes cannot be empty."));
        }
        final var result = new MapExpression();
        attributeNodes.stream()
                .map(attrNode -> this.getAttrExpression(attrNode, state))
                .forEach(result::addMapEntryExpression);
        return result;
    }

    /* TYPED COMPONENT CREATE: component constructor */

    // arg0, arg1, arg2, etc
    protected List<Expression> getConstructorArgs(ComponentConstructorNode componentConstructorNode) {
        final ConvertResult convertResult = GroovyUtil.convert(componentConstructorNode.getGroovyCode()
                .getAsValidGroovyCode());
        final BlockStatement blockStatement = convertResult.blockStatement();
        if (blockStatement == null) {
            throw new WebViewComponentBugError(new IllegalStateException("Did not expect blockStatement to be null."));
        }
        final List<Statement> statements = blockStatement.getStatements();
        if (statements.size() != 1) {
            throw new WebViewComponentBugError(new IllegalStateException("statements.size() != 1"));
        }
        final ExpressionStatement exprStmt = (ExpressionStatement) statements.getFirst();
        final ListExpression listExpr = (ListExpression) exprStmt.getExpression();
        return listExpr.getExpressions(); // TODO: pos for each expression
    }

    /* COMPONENT CHILDREN */

    // c0cc.add (jString | gString | component)
    protected Statement getChildCollectorAdd(Variable childCollector, Expression toAdd) {
        final VariableExpression childCollectorVariableExpr = new VariableExpression(childCollector);
        final MethodCallExpression methodCall = new MethodCallExpression(
                childCollectorVariableExpr,
                "add",
                new ArgumentListExpression(List.of(toAdd))
        );
        return new ExpressionStatement(methodCall);
    }

    // { WebViewComponentChildCollector c0cc -> ... }
    protected ClosureExpression getChildCollectorClosure(
            BodyNode bodyNode,
            TranspilerState state
    ) {
        final Parameter ccParam = new Parameter(
                CHILD_COLLECTOR_TYPE,
                this.getComponentName(state.getCurrentComponentNumber()) + "cc"
        );

        final var scope = state.pushScope();
        scope.putDeclaredVariable(ccParam);
        state.pushChildCollector(ccParam);

        final BlockStatement bodyStatements = this.getBodyTranspiler().transpileBody(
                bodyNode,
                (sourceNode, expr) -> this.getChildCollectorAdd(ccParam, expr),
                state
        );

        // clean up
        state.popChildCollector();
        state.popScope();

        return new ClosureExpression(
                new Parameter[] { ccParam },
                bodyStatements
        );
    }

    protected StaticMethodCallExpression getChildCollectorGetter(
            BodyNode bodyNode,
            TranspilerState state
    ) {
        final ArgumentListExpression args = new ArgumentListExpression();
        args.addExpression(VariableExpression.THIS_EXPRESSION);
        args.addExpression(this.getChildCollectorClosure(bodyNode, state));

        return new StaticMethodCallExpression(
                CHILD_COLLECTOR_CLOSURE_TYPE,
                "get",
                args
        );
    }

    /* TYPED COMPONENT CREATE: expression and statement */

    // context.create(...) {...}
    protected MethodCallExpression getTypedComponentCreateExpression(
            TypedComponentNode componentNode,
            TranspilerState state
    ) {
        final var createArgs = new ArgumentListExpression();

        createArgs.addExpression(new VariableExpression(state.getCurrentResolved()));

        final List<AttrNode> attributeNodes = componentNode.getArgs().getAttributes();
        if (!attributeNodes.isEmpty()) {
            createArgs.addExpression(this.getAttrMap(attributeNodes, state));
        }
        final ComponentConstructorNode constructorNode = componentNode.getArgs().getConstructor();
        if (constructorNode != null) {
            this.getConstructorArgs(constructorNode).forEach(createArgs::addExpression);
        }

        final @Nullable BodyNode bodyNode = componentNode.getBody();
        if (bodyNode != null) {
            createArgs.addExpression(this.getChildCollectorGetter(bodyNode, state));
        }

        return new MethodCallExpression(new VariableExpression(state.getRenderContext()), "create", createArgs);
    }

    // c0 = context.create(context.resolve(''), [:], ...) {...}
    protected ExpressionStatement getTypedComponentCreateStatement(
            TypedComponentNode componentNode,
            TranspilerState state
    ) {
        final var left = new VariableExpression(state.getCurrentComponent());
        final var right = this.getTypedComponentCreateExpression(componentNode, state);
        final var componentAssignExpr = new BinaryExpression(left, getAssignToken(), right);
        return new ExpressionStatement(componentAssignExpr);
    }

    /* CREATE CATCH */

    // catch (ComponentCreateException c0CreateException) { ... }
    protected CatchStatement getTypedCreateCatch(TypedComponentNode componentNode, TranspilerState state) {
        final String exceptionName = this.getComponentName(state.getCurrentComponentNumber()) + "CreateException";
        final Parameter exceptionParam = new Parameter(COMPONENT_CREATE_EXCEPTION_TYPE, exceptionName);
        final VariableExpression exceptionVar = new VariableExpression(exceptionName);

        final VariableScope scope = new VariableScope();
        scope.putDeclaredVariable(exceptionParam);

        final List<Statement> statements = new ArrayList<>();

        final BinaryExpression setTemplateExpression = new BinaryExpression(
                new PropertyExpression(exceptionVar, "template"),
                getAssignToken(),
                VariableExpression.THIS_EXPRESSION
        );
        statements.add(new ExpressionStatement(setTemplateExpression));

        final SourcePosition start = componentNode.getTokenRange().getStartPosition();

        final BinaryExpression setLineExpression = new BinaryExpression(
                new PropertyExpression(exceptionVar, "line"),
                getAssignToken(),
                new ConstantExpression(start.line())
        );
        statements.add(new ExpressionStatement(setLineExpression));

        final BinaryExpression setColumnExpression = new BinaryExpression(
                new PropertyExpression(exceptionVar, "column"),
                getAssignToken(),
                new ConstantExpression(start.column())
        );
        statements.add(new ExpressionStatement(setColumnExpression));

        statements.add(new ThrowStatement(exceptionVar));

        return new CatchStatement(exceptionParam, new BlockStatement(statements, scope));
    }

    protected List<Statement> getTypedCreateStatements(TypedComponentNode componentNode, TranspilerState state) {
        final Statement declaration = this.getTypedComponentDeclaration(state);
        final TryCatchStatement createTryCatch = new TryCatchStatement(
                this.getTypedComponentCreateStatement(componentNode, state),
                EmptyStatement.INSTANCE
        );
        createTryCatch.addCatch(this.getTypedCreateCatch(componentNode, state));
        return List.of(declaration, createTryCatch);
    }

    /* FRAGMENT COMPONENT */

    // context.createFragment(new Fragment(), <child cl>)
    protected MethodCallExpression getFragmentCreateExpression(
            FragmentComponentNode componentNode,
            TranspilerState state
    ) {
        final Expression fragmentConstructor = new ConstructorCallExpression(
                FRAGMENT_TYPE,
                ArgumentListExpression.EMPTY_ARGUMENTS
        );
        final Expression ccClosure = this.getChildCollectorGetter(componentNode.getBody(), state);

        final ArgumentListExpression args = new ArgumentListExpression(List.of(fragmentConstructor, ccClosure));

        return new MethodCallExpression(
                new VariableExpression(state.getRenderContext()),
                "createFragment",
                args
        );
    }

    /* MAIN */

    @Override
    public List<Statement> createComponentStatements(ComponentNode componentNode, TranspilerState state) {
        if (componentNode instanceof TypedComponentNode typedComponentNode) {
            // Resolve
            final List<Statement> resolveStatements = this.getResolveStatements(typedComponentNode, state);
            // Create
            final List<Statement> createStatements = this.getTypedCreateStatements(typedComponentNode, state);
            // Append/Add
            final Statement addOrAppend = this.getAppendOrAddStatementFactory().addOrAppend(
                    componentNode,
                    state,
                    new VariableExpression(state.getCurrentComponent())
            );

            // cleanup
            state.popResolved();
            state.popComponent();

            final List<Statement> allStatements = new ArrayList<>();
            allStatements.addAll(resolveStatements);
            allStatements.addAll(createStatements);
            allStatements.add(addOrAppend);

            return allStatements;
        } else if (componentNode instanceof FragmentComponentNode fragmentComponentNode) {
            // Create and add all at once
            final Statement addOrAppend = this.getAppendOrAddStatementFactory().addOrAppend(
                    componentNode,
                    state,
                    this.getFragmentCreateExpression(fragmentComponentNode, state)
            );
            return List.of(addOrAppend);
        } else {
            throw new WebViewComponentBugError(new IllegalArgumentException(
                    "Cannot handle a ComponentNode not of type TypedComponentNode or FragmentComponentNode."
            ));
        }
    }

}
