package groowt.util.di;

import java.util.function.Supplier;

public record LazySingletonBinding<T>(Supplier<T> singletonSupplier) implements Binding<T> {}
