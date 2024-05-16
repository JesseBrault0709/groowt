package groowt.util.fp.provider;

import java.util.List;
import java.util.function.Function;

public interface ListProvider<T> {

    Class<T> getType();

    <U> ListProvider<U> map(Class<U> targetType, Function<? super T, ? extends U> mapper);
    <U> ListProvider<U> flatMap(Class<U> targetType, Function<? super T, ? extends Provider<? extends U>> flatMapper);

    <U extends T> ListProvider<U> withType(Class<U> desiredType);

    List<T> get();

}
