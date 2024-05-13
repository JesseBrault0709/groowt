package groowt.util.fp.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultListProvider<T> implements ListProvider<T> {

    private final List<Provider<T>> elementProviders;
    private final List<Predicate<? super T>> filters;

    public DefaultListProvider(List<? extends Provider<T>> elementProviders) {
        this.elementProviders = new ArrayList<>(elementProviders);
        this.filters = List.of();
    }

    public DefaultListProvider(
            List<? extends Provider<T>> elementProviders,
            List<? extends Predicate<? super T>> filters
    ) {
        this.elementProviders = new ArrayList<>(elementProviders);
        this.filters = new ArrayList<>(filters);
    }

    private DefaultListProvider(DefaultListProvider<T> old, Predicate<? super T> filterToAdd) {
        this.elementProviders = new ArrayList<>(old.elementProviders);
        this.filters = new ArrayList<>(old.filters);
        this.filters.add(filterToAdd);
    }

    @Override
    public ListProvider<T> filterElements(Predicate<? super T> predicate) {
        return new DefaultListProvider<>(this, predicate);
    }

    @Override
    public <U> ListProvider<U> mapElements(Function<? super T, ? extends U> mapper) {
        final List<Provider<U>> uProviders = this.elementProviders.stream()
                .<Provider<U>>map(elementProvider -> elementProvider.map(mapper))
                .toList();
        return new DefaultListProvider<>(uProviders);
    }

    @Override
    public <U> ListProvider<U> flatMapElements(Function<? super T, ? extends Provider<? extends U>> flatMapper) {
        final List<Provider<U>> uProviders = this.elementProviders.stream()
                .map(elementProvider -> elementProvider.flatMap(flatMapper))
                .toList();
        return new DefaultListProvider<>(uProviders);
    }

    @Override
    public List<T> get() {
        final List<? extends T> filtered = this.elementProviders.stream()
                .map(Provider::get)
                .filter(element -> {
                    for (final var filter : this.filters) {
                        if (!filter.test(element)) {
                            return false;
                        }
                    }
                    return true;
                }).toList();
        return new ArrayList<>(filtered);
    }

}
