package groowt.util.di;

sealed public interface Binding<T> permits ClassBinding, ProviderBinding, SingletonBinding, LazySingletonBinding {}
