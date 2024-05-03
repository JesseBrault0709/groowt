package groowt.util.di;

public record ClassBinding<T>(Class<T> from, Class<? extends T> to) implements Binding<T> {}
