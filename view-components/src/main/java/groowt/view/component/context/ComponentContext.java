package groowt.view.component.context;

import groowt.view.component.ViewComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface ComponentContext {

    List<ComponentScope> getScopeStack();

    void pushScope(ComponentScope scope);
    void pushDefaultScope();
    void popScope();
    ComponentScope getRootScope();

    default ComponentScope getCurrentScope() {
        final List<ComponentScope> scopeStack = this.getScopeStack();
        if (scopeStack.isEmpty()) {
            throw new NullPointerException("There is no current scope.");
        }
        return scopeStack.getFirst();
    }

    @Nullable ViewComponent getParent();
    @Nullable <T extends ViewComponent> T getParent(Class<T> parentClass);

    @Nullable ViewComponent findNearestAncestor(Predicate<? super ViewComponent> matching);

    default <T extends ViewComponent> @Nullable T findNearestAncestor(
            Class<T> ancestorClass,
            Predicate<? super ViewComponent> matching
    ) {
        return ancestorClass.cast(this.findNearestAncestor(matching.and(ancestorClass::isInstance)));
    }

    List<ViewComponent> getAllAncestors();

}
