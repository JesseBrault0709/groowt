package groowt.view.component;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class DefaultComponentContext implements ComponentContext {

    private final Deque<ComponentScope> scopeStack = new LinkedList<>();
    private final Deque<ViewComponent> componentStack = new LinkedList<>();

    @Override
    public ComponentFactory<?> resolve(String component) {
        if (scopeStack.isEmpty()) {
            throw new IllegalStateException("There are no scopes on the scopeStack.");
        }

        final var getStack = new LinkedList<>(this.scopeStack);
        while (!getStack.isEmpty()) {
            final ComponentScope scope = getStack.pop();
            if (scope.contains(component)) {
                return scope.get(component);
            }
        }

        final var missingStack = new LinkedList<>(this.scopeStack);
        NoFactoryMissingException first = null;
        while (!missingStack.isEmpty()) {
            final ComponentScope scope = getStack.pop();
            try {
                return scope.factoryMissing(component);
            } catch (NoFactoryMissingException e) {
                if (first == null) {
                    first = e;
                }
            }
        }

        if (first == null) {
            throw new IllegalStateException("First FactoryMissingException is still null.");
        }

        throw first;
    }

    @Override
    public ViewComponent create(ComponentFactory<?> factory, Object... args) {
        final ViewComponent component = factory.create(this, args);
        this.componentStack.push(component);
        return component;
    }

    @Override
    public void afterComponent(ViewComponent component) {
        final var popped = this.componentStack.pop();
        if (!popped.equals(component)) {
            throw new IllegalStateException("Popped component does not equal arg to afterComponent()");
        }
    }

    @Override
    public Deque<ComponentScope> getScopeStack() {
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
    public Deque<ViewComponent> getComponentStack() {
        return new LinkedList<>(this.componentStack);
    }

    @Override
    public @Nullable ViewComponent getParent() {
        if (this.componentStack.size() > 1) {
            final var child = this.componentStack.pop();
            final var parent = this.componentStack.pop();
            this.componentStack.push(parent);
            this.componentStack.push(child);
            return parent;
        }
        return null;
    }

    @Override
    public <T extends ViewComponent> @Nullable T getParent(Class<T> parentClass) {
        return parentClass.cast(this.getParent());
    }

    @Override
    public @Nullable ViewComponent findNearestAncestor(Predicate<? super ViewComponent> matching) {
        if (this.componentStack.size() > 1) {
            final Deque<ViewComponent> tmp = new LinkedList<>();
            tmp.push(this.componentStack.pop()); // child
            ViewComponent result = null;
            while (result == null && !this.componentStack.isEmpty()) {
                final var ancestor = this.componentStack.pop();
                tmp.push(ancestor);
                if (matching.test(ancestor)) {
                    result = ancestor;
                }
            }
            while (!tmp.isEmpty()) {
                this.componentStack.push(tmp.pop());
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ViewComponent> getAllAncestors() {
        if (this.componentStack.size() > 1) {
            final var child = this.componentStack.pop();
            final List<ViewComponent> result = new ArrayList<>(this.componentStack);
            this.componentStack.push(child);
            return result;
        } else {
            return List.of();
        }
    }

}
