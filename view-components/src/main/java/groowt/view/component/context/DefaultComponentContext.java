package groowt.view.component.context;

import groowt.view.component.ViewComponent;
import groowt.view.component.runtime.RenderContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class DefaultComponentContext implements ComponentContext {

    private final LinkedList<ComponentScope> scopeStack = new LinkedList<>();
    private RenderContext renderContext;

    @ApiStatus.Internal
    public RenderContext getRenderContext() {
        return Objects.requireNonNull(
                this.renderContext,
                "The renderContext is null. Did this method get called from outside of a rendering context?"
        );
    }

    @ApiStatus.Internal
    public void setRenderContext(RenderContext renderContext) {
        this.renderContext = Objects.requireNonNull(renderContext);
    }

    @Override
    public List<ComponentScope> getScopeStack() {
        return new LinkedList<>(this.scopeStack);
    }

    @Override
    public void pushScope(ComponentScope scope) {
        this.scopeStack.push(scope);
    }

    protected ComponentScope getNewDefaultScope() {
        return new DefaultComponentScope();
    }

    @Override
    public void pushDefaultScope() {
        this.pushScope(this.getNewDefaultScope());
    }

    @Override
    public void popScope() {
        this.scopeStack.pop();
    }

    @Override
    public ComponentScope getRootScope() {
        return this.scopeStack.getLast();
    }

    @Override
    public @Nullable ViewComponent getParent() {
        final List<ViewComponent> componentStack = this.getRenderContext().getComponentStack();
        if (componentStack.size() > 1) {
            return componentStack.get(1);
        }
        return null;
    }

    @Override
    public <T extends ViewComponent> @Nullable T getParent(Class<T> parentClass) {
        return parentClass.cast(this.getParent());
    }

    @Override
    public @Nullable ViewComponent findNearestAncestor(Predicate<? super ViewComponent> matching) {
        final List<ViewComponent> componentStack = this.getRenderContext().getComponentStack();
        if (componentStack.size() > 1) {
            for (final var ancestor : componentStack.subList(1, componentStack.size() -1)) {
                if (matching.test(ancestor)) {
                    return ancestor;
                }
            }
        }
        return null;
    }

    @Override
    public List<ViewComponent> getAllAncestors() {
        final List<ViewComponent> componentStack = this.getRenderContext().getComponentStack();
        return componentStack.subList(1, componentStack.size());
    }

}
