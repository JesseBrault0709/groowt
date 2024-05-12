package groowt.view.component.context;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FirstParam;
import groovy.transform.stc.SimpleType;
import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.RenderContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface ComponentContext {

    RenderContext getRenderContext();

    List<ComponentScope> getScopeStack();

    void pushScope(ComponentScope scope);

    void pushDefaultScope();

    void popScope();

    ComponentScope getRootScope();

    default <S extends ComponentScope> S getRootScope(Class<? extends S> scopeClass) {
        return scopeClass.cast(this.getRootScope());
    }

    default void configureRootScope(
            @ClosureParams(value = SimpleType.class, options = "groowt.view.component.context.ComponentScope")
            @DelegatesTo(ComponentScope.class)
            Closure<?> configure
    ) {
        final var rootScope = this.getRootScope();
        configure.setDelegate(rootScope);
        configure.call(rootScope);
    }

    default <S extends ComponentScope> void configureRootScope(
            Class<S> scopeClass,
            @ClosureParams(value = FirstParam.FirstGenericType.class)
            @DelegatesTo(type = "S")
            Closure<?> configure
    ) {
        final var rootScope = this.getRootScope(scopeClass);
        configure.setDelegate(rootScope);
        configure.call(rootScope);
    }

    default ComponentScope getCurrentScope() {
        final List<ComponentScope> scopeStack = this.getScopeStack();
        if (scopeStack.isEmpty()) {
            throw new NullPointerException("There is no current scope.");
        }
        return scopeStack.getFirst();
    }

    default <S extends ComponentScope> S getCurrentScope(Class<? extends S> scopeClass) {
        return scopeClass.cast(this.getCurrentScope());
    }

    default void configureCurrentScope(
            @ClosureParams(value = SimpleType.class, options = "groowt.view.component.context.ComponentScope")
            @DelegatesTo(ComponentScope.class)
            Closure<?> configure
    ) {
        final var currentScope = this.getCurrentScope();
        configure.setDelegate(currentScope);
        configure.call(currentScope);
    }

    default <S extends ComponentScope> void configureCurrentScope(
            Class<S> scopeClass,
            @ClosureParams(value = FirstParam.FirstGenericType.class)
            @DelegatesTo(type = "S")
            Closure<?> configure
    ) {
        final var currentScope = this.getCurrentScope(scopeClass);
        configure.setDelegate(currentScope);
        configure.call(currentScope);
    }

    @Nullable ViewComponent getParent();

    default <T extends ViewComponent> @Nullable T getParent(Class<T> parentClass) {
        return parentClass.cast(this.getParent());
    }

    @Nullable ViewComponent findNearestAncestor(Predicate<? super ViewComponent> matching);

    default <T extends ViewComponent> @Nullable T findNearestAncestor(
            Class<T> ancestorClass,
            Predicate<? super ViewComponent> matching
    ) {
        return ancestorClass.cast(this.findNearestAncestor(matching.and(ancestorClass::isInstance)));
    }

    List<ViewComponent> getAllAncestors();

}
