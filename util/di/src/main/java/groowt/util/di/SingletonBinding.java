package groowt.util.di;

public record SingletonBinding<T>(T to) implements Binding<T> {}
