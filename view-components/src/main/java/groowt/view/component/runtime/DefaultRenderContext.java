package groowt.view.component.runtime;

import groowt.view.component.ViewComponent;
import groowt.view.component.ViewComponentBugError;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.context.ComponentResolveException;
import groowt.view.component.context.ComponentScope.TypeAndFactory;

import java.util.LinkedList;
import java.util.List;

public class DefaultRenderContext implements RenderContext {

    private final ComponentContext componentContext;
    private final ComponentWriter writer;
    private final LinkedList<ViewComponent> componentStack = new LinkedList<>();

    public DefaultRenderContext(ComponentContext componentContext, ComponentWriter writer) {
        this.componentContext = componentContext;
        this.writer = writer;
    }

    public ComponentContext getComponentContext() {
        return this.componentContext;
    }

    @Override
    public Resolved<?> resolve(String typeName) throws ComponentResolveException {
        for (final var scope : this.getComponentContext().getScopeStack()) {
            if (scope.contains(typeName)) {
                try {
                    final var typeAndFactory = scope.get(typeName);
                    return new ResolvedStringType<>(typeName, typeAndFactory.type(), typeAndFactory.factory());
                } catch (Exception e) {
                    throw new ComponentResolveException(typeName, e);
                }
            }
        }
        Exception firstException = null;
        for (final var scope : this.getComponentContext().getScopeStack()) {
            try {
                final var typeAndFactory = (TypeAndFactory<?>) scope.factoryMissing(typeName);
                return new ResolvedStringType<>(typeName, typeAndFactory.type(), typeAndFactory.factory());
            } catch (Exception e) {
                if (firstException == null) {
                    firstException = e;
                }
            }
        }
        if (firstException != null) {
            throw new ComponentResolveException(typeName, firstException);
        } else {
            throw new ViewComponentBugError(
                    "Could not resolve factory for " + typeName + " and firstException was null."
            );
        }
    }

    @Override
    public <T extends ViewComponent> Resolved<T> resolve(String alias, Class<T> type) throws ComponentResolveException {
        for (final var scope : this.getComponentContext().getScopeStack()) {
            if (scope.contains(type)) {
                try {
                    final var typeAndFactory = (TypeAndFactory<T>) scope.get(type);
                    return new ResolvedClassType<>(
                            alias,
                            type,
                            typeAndFactory.type(),
                            typeAndFactory.factory()
                    );
                } catch (Exception e) {
                    throw new ComponentResolveException(alias, type, e);
                }
            }
        }
        throw new ComponentResolveException(
                "Could not find a factory for " + alias + " of type " + type.getName() + " in scope.",
                alias,
                type
        );
    }

    @Override
    public ViewComponent create(Resolved<?> resolved, Object... args) {
        final ViewComponent created;
        if (resolved instanceof ResolvedStringType<?> resolvedStringType) {
            try {
                created = resolvedStringType.componentFactory().create(
                        resolvedStringType.typeName(),
                        this.getComponentContext(),
                        args
                );
            } catch (Exception createException) {
                throw new ComponentCreateException(resolved, createException);
            }
        } else if (resolved instanceof ResolvedClassType<?> resolvedClassType) {
            try {
                created = resolvedClassType.componentFactory().create(
                        resolvedClassType.alias(),
                        resolvedClassType.resolvedType(),
                        this.getComponentContext(),
                        args
                );
            } catch (Exception createException) {
                throw new ComponentCreateException(resolved, createException);
            }
        } else {
            throw new UnsupportedOperationException(
                    this.getClass().getName() + " cannot handle Resolved of sub-type " + resolved.getClass().getName()
            );
        }
        created.setContext(this.getComponentContext());
        return created;
    }

    @Override
    public void pushComponent(ViewComponent component) {
        this.componentStack.push(component);
    }

    @Override
    public void popComponent(ViewComponent component) {
        final var popped = this.componentStack.pop();
        if (!popped.equals(component)) {
            throw new ViewComponentBugError(
                    "Popped component != expected component; popped: " + popped + ", expected: " + component + "."
            );
        }
    }

    @Override
    public List<ViewComponent> getComponentStack() {
        return new LinkedList<>(this.componentStack);
    }

    @Override
    public ComponentWriter getWriter() {
        return this.writer;
    }

}
