package groowt.util.fp.provider;

import java.util.Set;
import java.util.function.Function;

public interface SetProvider<T> {

    Class<T> getType();

    Set<Provider<T>> getProviders();

    <U> SetProvider<U> map(Class<U> targetType, Function<? super T, ? extends U> mapper);

    <U> SetProvider<U> flatMap(Class<U> targetType, Function<? super T, ? extends Provider<U>> mapper);

    <U> SetProvider<U> withType(Class<U> desiredType);

    Set<T> get();

    SetProvider<T> zip(SetProvider<? extends T> other);

}
