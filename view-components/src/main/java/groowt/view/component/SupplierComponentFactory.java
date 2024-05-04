package groowt.view.component;

import java.util.function.Supplier;

final class SupplierComponentFactory<T extends ViewComponent> extends ComponentFactoryBase<T> {

    private final Supplier<T> tSupplier;

    public SupplierComponentFactory(Supplier<T> tSupplier) {
        this.tSupplier = tSupplier;
    }

    public T doCreate(Object type, ComponentContext componentContext, Object... args) {
        return this.tSupplier.get();
    }

}
