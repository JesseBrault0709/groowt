package groowt.util.fp.provider;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ListProvider<T> extends Provider<List<T>> {

    <U> ListProvider<U> mapElements(Function<? super T, ? extends U> mapper);
    <U> ListProvider<U> flatMapElements(Function<? super T, ? extends Provider<? extends U>> flatMapper);

    ListProvider<T> filterElements(Predicate<? super T> predicate);

    default <R extends T> ListProvider<R> filterElementsByType(Class<? extends R> type) {
        return this.filterElements(type::isInstance).mapElements(type::cast);
    }

}
