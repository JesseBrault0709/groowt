package groowt.util.di;

import jakarta.inject.Provider;

public record ProviderBinding<T>(Class<T> to, Provider<? extends T> provider) implements Binding<T> {}
