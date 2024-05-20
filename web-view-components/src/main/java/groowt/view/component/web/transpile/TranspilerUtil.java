package groowt.view.component.web.transpile;

import groovy.lang.Tuple2;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.web.WebViewComponent;
import groowt.view.component.web.runtime.WebViewComponentRenderContext;
import groowt.view.component.web.util.SourcePosition;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class TranspilerUtil {

    public static final ClassNode COMPONENT_TEMPLATE = ClassHelper.make(ComponentTemplate.class);
    public static final ClassNode COMPONENT_CONTEXT_TYPE = ClassHelper.make(ComponentContext.class);
    public static final ClassNode COMPONENT_WRITER_TYPE = ClassHelper.make(ComponentWriter.class);
    public static final ClassNode WEB_VIEW_COMPONENT_RENDER_CONTEXT_TYPE = ClassHelper.make(WebViewComponentRenderContext.class);
    public static final ClassNode WEB_VIEW_COMPONENT_TYPE = ClassHelper.make(WebViewComponent.class);

    public static final String GROOWT_VIEW_COMPONENT_WEB = "groowt.view.component.web";
    public static final String COMPONENT_CONTEXT_NAME = "componentContext";
    public static final String COMPONENT_WRITER_NAME = "out";
    public static final String RENDER_CONTEXT_NAME = "renderContext";
    public static final String GET_RENDERER = "getRenderer";
    public static final String APPEND = "append";
    public static final String ADD = "add";

    public static Tuple2<ConstantExpression, ConstantExpression> lineAndColumn(SourcePosition sourcePosition) {
        return new Tuple2<>(
                new ConstantExpression(sourcePosition.line(), true),
                new ConstantExpression(sourcePosition.column(), true)
        );
    }

    public static ConstantExpression getStringLiteral(String content) {
        final var e = new ConstantExpression(content);
        e.setNodeMetaData("_IS_STRING", true);
        return e;
    }

    public static Token getAssignToken() {
        return new Token(Types.ASSIGN, "=", -1, -1);
    }

    public static Token getLeftShiftToken() {
        return new Token(Types.LEFT_SHIFT, "<<", -1, -1);
    }

    public static final class TranspilerState {

        public static TranspilerState withRootScope(
                Parameter componentContext,
                Parameter writer,
                Variable renderContext
        ) {
            final VariableScope rootScope = new VariableScope();
            rootScope.putDeclaredVariable(componentContext);
            rootScope.putDeclaredVariable(writer);
            rootScope.putDeclaredVariable(renderContext);

            return new TranspilerState(rootScope);
        }

        @TestOnly
        public static TranspilerState withDefaultRootScope() {
            final VariableScope rootScope = new VariableScope();
            rootScope.putDeclaredVariable(new Parameter(COMPONENT_CONTEXT_TYPE, COMPONENT_CONTEXT_NAME));
            rootScope.putDeclaredVariable(new Parameter(COMPONENT_WRITER_TYPE, COMPONENT_WRITER_NAME));
            rootScope.putDeclaredVariable(new VariableExpression(RENDER_CONTEXT_NAME,
                    WEB_VIEW_COMPONENT_RENDER_CONTEXT_TYPE
            ));
            return new TranspilerState(rootScope);
        }

        private final AtomicInteger componentNumberCounter = new AtomicInteger();
        private final Deque<VariableScope> scopeStack = new LinkedList<>();
        private final Deque<VariableExpression> componentStack = new LinkedList<>();
        private final Deque<VariableExpression> resolvedStack = new LinkedList<>();
        private final Deque<Parameter> childListStack = new LinkedList<>();
        private final List<ComponentTemplateCompileException> errors = new ArrayList<>();

        private int lastComponentNumber;

        private TranspilerState(VariableScope rootScope) {
            this.scopeStack.push(rootScope);
        }

        public int getCurrentComponentNumber() {
            return this.lastComponentNumber;
        }

        public int newComponentNumber() {
            this.lastComponentNumber = this.componentNumberCounter.getAndIncrement();
            return this.lastComponentNumber;
        }

        public VariableScope pushScope() {
            final VariableScope parent = this.scopeStack.peek();
            final VariableScope result = new VariableScope(parent);
            this.scopeStack.push(result);
            return result;
        }

        public void popScope() {
            this.scopeStack.pop();
        }

        public VariableScope getCurrentScope() {
            return Objects.requireNonNull(this.scopeStack.peek());
        }

        private Variable getDeclaredVariable(String name) {
            VariableScope scope = this.getCurrentScope();
            while (scope != null) {
                final Variable potential = scope.getDeclaredVariable(name);
                if (potential != null) {
                    return potential;
                } else {
                    scope = scope.getParent();
                }
            }
            throw new NullPointerException("Cannot find variable: " + name);
        }

        public VariableExpression getWriter() {
            return new VariableExpression(this.getDeclaredVariable(COMPONENT_WRITER_NAME));
        }

        public VariableExpression getRenderContext() {
            return (VariableExpression) this.getDeclaredVariable(RENDER_CONTEXT_NAME);
        }

        public void pushComponent(VariableExpression componentVariable) {
            this.componentStack.push(componentVariable);
        }

        public void popComponent() {
            this.componentStack.pop();
        }

        public VariableExpression getCurrentComponent() {
            return Objects.requireNonNull(this.componentStack.peek());
        }

        public void pushResolved(VariableExpression resolvedVariable) {
            this.resolvedStack.push(resolvedVariable);
        }

        public void popResolved() {
            this.resolvedStack.pop();
        }

        public VariableExpression getCurrentResolved() {
            return Objects.requireNonNull(this.resolvedStack.peek());
        }

        public void pushChildList(Parameter childCollector) {
            this.childListStack.push(childCollector);
        }

        public void popChildList() {
            this.childListStack.pop();
        }

        public VariableExpression getCurrentChildList() {
            final Parameter childCollectorParam = Objects.requireNonNull(this.childListStack.peek());
            return new VariableExpression(childCollectorParam);
        }

        public boolean hasCurrentChildList() {
            return this.childListStack.peek() != null;
        }

        public void addError(ComponentTemplateCompileException error) {
            this.errors.add(error);
        }

        public void addErrors(Collection<? extends ComponentTemplateCompileException> errors) {
            this.errors.addAll(errors);
        }

        public boolean hasErrors() {
            return !this.errors.isEmpty();
        }

        public List<ComponentTemplateCompileException> getErrors() {
            return new ArrayList<>(this.errors);
        }

    }

    private TranspilerUtil() {}

}
