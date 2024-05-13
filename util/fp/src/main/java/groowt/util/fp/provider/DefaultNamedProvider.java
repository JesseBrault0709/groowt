package groowt.util.fp.provider;

public class DefaultNamedProvider<T> implements NamedProvider<T> {

    private final String name;
    private final Provider<T> delegate;

    public DefaultNamedProvider(String name, T element) {
        this.name = name;
        this.delegate = Provider.of(element);
    }

    public DefaultNamedProvider(String name, Provider<T> delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T get() {
        return this.delegate.get();
    }

}
