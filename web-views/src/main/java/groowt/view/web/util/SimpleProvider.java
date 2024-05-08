package groowt.view.web.util;

final class SimpleProvider<T> implements Provider<T> {

    private final T t;

    public SimpleProvider(T t) {
        this.t = t;
    }

    @Override
    public T get() {
        if (this.t == null) {
            throw new NullPointerException("This Provider has a null value.");
        }
        return t;
    }

}
