package groowt.util.fp.property;

import groovy.lang.Closure;
import groowt.util.fp.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class SimpleProperty<T> implements Property<T> {

    private final List<Closure<?>> configureClosures = new ArrayList<>();

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
    public void configure(Closure<?> configureClosure) {
        this.configureClosures.add(configureClosure);
    }

    private void doConfigures(T t) {
        for (final var configureClosure : this.configureClosures) {
            configureClosure.setDelegate(t);
            configureClosure.call(t);
        }
    }

    @Override
    public T get() {
        if (!this.isPresent()) {
            throw new NullPointerException("Cannot get() from an empty Property. Set the value or set the convention.");
        }
        final T t;
        if (this.provider != null) {
            t = this.provider.get();
        } else {
            t = this.convention.get();
        }
        this.doConfigures(t);
        return t;
    }

}
