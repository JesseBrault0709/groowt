package groowt.util.fp.provider;

class DefaultNamedProvider<T> implements NamedProvider<T> {

    private final Class<T> type;
    private final String name;
    private final Provider<T> delegate;

    @SuppressWarnings("unchecked")
    public DefaultNamedProvider(String name, T element) {
        this.type = (Class<T>) element.getClass();
        this.name = name;
        this.delegate = DefaultProvider.of(element);
    }

    public DefaultNamedProvider(Class<T> type, String name, Provider<T> delegate) {
        this.type = type;
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public Class<T> getType() {
        return this.type;
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
