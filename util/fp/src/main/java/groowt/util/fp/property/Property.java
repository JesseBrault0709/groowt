package groowt.util.fp.property;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groowt.util.fp.provider.Provider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface Property<T> extends Provider<T> {

    static <T> Property<T> empty() {
        return new SimpleProperty<>();
    }

    static <T> Property<T> of(T t) {
        final Property<T> property = new SimpleProperty<>();
        property.set(t);
        return property;
    }

    static <T> Property<T> ofProvider(Provider<T> tProvider) {
        final Property<T> property = new SimpleProperty<>();
        property.set(tProvider);
        return property;
    }

    static <T> Property<T> ofLazy(Supplier<T> tSupplier) {
        final Property<T> property = new SimpleProperty<>();
        property.set(Provider.ofLazy(tSupplier));
        return property;
    }

    void set(T t);
    void set(Provider<? extends T> tProvider);
    void setConvention(T t);
    void setConvention(Provider<? extends T> tProvider);

    void configure(
            @DelegatesTo(type = "T")
            @ClosureParams(value = FromString.class, options = "T")
            Closure<?> configureClosure
    );

    boolean isPresent();
    boolean isEmpty();

    default T fold(@Nullable T onEmpty) {
        if (this.isPresent()) {
            return this.get();
        } else {
            return onEmpty;
        }
    }

    default T fold(Provider<? extends T> onEmpty) {
        if (this.isPresent()) {
            return this.get();
        } else {
            return onEmpty.get();
        }
    }

}
