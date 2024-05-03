package groowt.view.component;

final class DelegatingComponentFactory<T extends ViewComponent> extends AbstractComponentFactory<T> {

    @FunctionalInterface
    interface ComponentFactoryDelegate<T extends ViewComponent> {
        T doCreate(ComponentContext context, Object... args);
    }

    private final ComponentFactoryDelegate<T> function;

    public DelegatingComponentFactory(ComponentFactoryDelegate<T> function) {
        this.function = function;
    }

    public T doCreate(ComponentContext componentContext, Object... args) {
        return this.function.doCreate(componentContext, args);
    }

}
