package groowt.view.component.context;

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
