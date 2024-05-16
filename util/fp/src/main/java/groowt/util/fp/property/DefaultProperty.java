package groowt.util.fp.property;

import groovy.lang.Closure;
import groowt.util.fp.provider.DefaultProvider;
import groowt.util.fp.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class DefaultProperty<T> implements Property<T> {

    public static <T> Property<T> empty(Class<T> type) {
        return new DefaultProperty<>(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> Property<T> of(T t) {
        final Property<T> property = new DefaultProperty<>((Class<T>) t.getClass());
        property.set(t);
        return property;
    }

    public static <T> Property<T> ofProvider(Class<T> type, Provider<T> tProvider) {
        final Property<T> property = new DefaultProperty<>(type);
        property.set(tProvider);
        return property;
    }

    public static <T> Property<T> ofLazy(Class<T> type, Supplier<T> tSupplier) {
        final Property<T> property = new DefaultProperty<>(type);
        property.set(DefaultProvider.ofLazy(type, tSupplier));
        return property;
    }

    private final Class<T> type;
    private final List<Closure<?>> configureClosures = new ArrayList<>();

    private Provider<? extends T> provider;
    private Provider<? extends T> convention;

    protected DefaultProperty(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

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
        requireNonNull(t);
        this.provider = DefaultProvider.of(t);
    }

    @Override
    public void set(Provider<? extends T> tProvider) {
        requireNonNull(tProvider);
        this.provider = tProvider;
    }

    @Override
    public void setConvention(T convention) {
        requireNonNull(convention);
        this.convention = DefaultProvider.of(convention);
    }

    @Override
    public void setConvention(Provider<? extends T> convention) {
        requireNonNull(convention);
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
