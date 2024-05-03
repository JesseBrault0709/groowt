package groowt.view.component;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface ComponentContext {

    @ApiStatus.Internal
    ComponentFactory<?> resolve(String component);

    @ApiStatus.Internal
    ViewComponent create(ComponentFactory<?> factory, Object... args);

    @ApiStatus.Internal
    void afterComponent(ViewComponent component);

    Deque<ComponentScope> getScopeStack();

    void pushScope(ComponentScope scope);
    void pushDefaultScope();
    void popScope();

    default ComponentScope getCurrentScope() {
        return Objects.requireNonNull(this.getScopeStack().peek(), "There is no current scope.");
    }

    Deque<ViewComponent> getComponentStack();

    @Nullable ViewComponent getParent();
    @Nullable <T extends ViewComponent> T getParent(Class<T> parentClass);

    @Nullable ViewComponent findNearestAncestor(Predicate<? super ViewComponent> matching);

    default <T extends ViewComponent> @Nullable T findNearestAncestor(
            Class<T> ancestorClass,
            Predicate<? super ViewComponent> matching
    ) {
        return ancestorClass.cast(matching.and(ancestorClass::isInstance));
    }

    default @Nullable ViewComponent findNearestAncestorByTypeName(String typeName) {
        return this.findNearestAncestor(component -> component.getTypeName().equals(typeName));
    }

    List<ViewComponent> getAllAncestors();

}
