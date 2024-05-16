package groowt.util.fp.property;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groowt.util.fp.provider.Provider;
import org.jetbrains.annotations.Nullable;

public interface Property<T> extends Provider<T> {

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
