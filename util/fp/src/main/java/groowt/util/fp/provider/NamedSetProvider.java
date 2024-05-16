package groowt.util.fp.provider;

import java.util.function.Predicate;

public interface NamedSetProvider<T> extends SetProvider<T> {

    NamedSetProvider<T> withNames(Predicate<? super String> namePredicate);
    NamedProvider<T> withName(String name);

    NamedSetProvider<T> zipWithNames(NamedSetProvider<? extends T> other);

}
