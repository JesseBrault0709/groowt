package groowt.view.web.transpile;

import groovy.lang.Tuple2;
import groowt.view.component.ComponentContext;
import groowt.view.component.ComponentTemplate;
import groowt.view.web.runtime.WebViewComponentWriter;
import groowt.view.web.util.SourcePosition;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class TranspilerUtil {

    public static final ClassNode COMPONENT_TEMPLATE = ClassHelper.make(ComponentTemplate.class);
    public static final ClassNode OUT_TYPE = ClassHelper.make(WebViewComponentWriter.class);
    public static final ClassNode CONTEXT_CLASSNODE = ClassHelper.make(ComponentContext.class);

    public static final String GROOWT_VIEW_WEB = "groowt.view.web";
    public static final String OUT = "out";
    public static final String CONTEXT = "context";
    public static final String GET_RENDERER = "getRenderer";

    public static Tuple2<ConstantExpression, ConstantExpression> lineAndColumn(SourcePosition sourcePosition) {
        return new Tuple2<>(
                new ConstantExpression(sourcePosition.line(), true),
                new ConstantExpression(sourcePosition.column(), true)
        );
    }

    public static ConstantExpression makeStringLiteral(String content) {
        final var e = new ConstantExpression(content);
        e.setNodeMetaData("_IS_STRING", true);
        return e;
    }

    public static final class TranspilerState {

        public static TranspilerState withDefaultRootScope() {
            final var contextParam = new Parameter(CONTEXT_CLASSNODE, CONTEXT);
            final var outParam = new Parameter(OUT_TYPE, OUT);

            final VariableScope rootScope = new VariableScope();
            rootScope.putDeclaredVariable(contextParam);
            rootScope.putDeclaredVariable(outParam);

            return new TranspilerState(rootScope);
        }

        public static TranspilerState withRootScope(VariableScope rootScope) {
            return new TranspilerState(rootScope);
        }

        private final AtomicInteger componentCounter = new AtomicInteger();
        private final Deque<VariableScope> scopeStack = new LinkedList<>();

        private TranspilerState(VariableScope rootScope) {
            this.scopeStack.push(rootScope);
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

        public VariableScope currentScope() {
            return Objects.requireNonNull(this.scopeStack.peek());
        }

        public int newComponentNumber() {
            return this.componentCounter.getAndIncrement();
        }

        public Variable out() {
            return this.getDeclaredVariable(OUT);
        }

        public Variable context() {
            return this.getDeclaredVariable(CONTEXT);
        }

        public Variable getDeclaredVariable(String name) {
            VariableScope scope = this.currentScope();
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

    }

    private TranspilerUtil() {}

}
