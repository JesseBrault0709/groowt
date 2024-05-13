package groowt.util.fp.property;

import groowt.util.fp.provider.Provider;

import java.util.Objects;

final class SimpleProperty<T> implements Property<T> {

    private Provider<? extends T> provider;
    private Provider<? extends T> convention;

    @Override
    public boolean isPresent() {
        return this.provider != null || this.convention != null;
    }

    @Override
    public boolean isEmpty() {
        return this.provider == null && this.convention == null;
    }

    @Override
    public void set(T t) {
        Objects.requireNonNull(t);
        this.provider = Provider.of(t);
    }

    @Override
    public void set(Provider<? extends T> tProvider) {
        Objects.requireNonNull(tProvider);
        this.provider = tProvider;
    }

    @Override
    public void setConvention(T convention) {
        Objects.requireNonNull(convention);
        this.convention = Provider.of(convention);
    }

    @Override
    public void setConvention(Provider<? extends T> convention) {
        Objects.requireNonNull(convention);
        this.convention = convention;
    }

    @Override
    public T get() {
        if (!this.isPresent()) {
            throw new NullPointerException("Cannot get() from an empty Property. Set the value or set the convention.");
        } else if (this.provider != null) {
            return this.provider.get();
        } else {
            return this.convention.get();
        }
    }

}
