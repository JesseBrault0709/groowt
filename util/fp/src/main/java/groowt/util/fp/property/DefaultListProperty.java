package groowt.util.fp.property;

import groowt.util.fp.provider.DefaultListProvider;
import groowt.util.fp.provider.ListProvider;
import groowt.util.fp.provider.Provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultListProperty<T> implements ListProperty<T> {

    private final List<Provider<T>> elementProviders = new ArrayList<>();

    @Override
    public void addElement(T element) {
        this.elementProviders.add(Provider.of(element));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addProvider(Provider<? extends T> elementProvider) {
        this.elementProviders.add((Provider<T>) elementProvider);
    }

    @Override
    public void addAllElements(Collection<? extends T> elements) {
        elements.forEach(this::addElement);
    }

    @Override
    public void addAllProviders(Collection<? extends Provider<? extends T>> elementProviders) {
        elementProviders.forEach(this::addProvider);
    }

    @Override
    public <U> ListProvider<U> mapElements(Function<? super T, ? extends U> mapper) {
        return new DefaultListProvider<>(
                this.elementProviders.stream()
                        .<Provider<U>>map(elementProvider -> elementProvider.map(mapper))
                        .toList()
        );
    }

    @Override
    public <U> ListProvider<U> flatMapElements(Function<? super T, ? extends Provider<? extends U>> flatMapper) {
        return new DefaultListProvider<>(
                this.elementProviders.stream()
                        .map(elementProvider -> elementProvider.flatMap(flatMapper))
                        .toList()
        );
    }

    @Override
    public ListProvider<T> filterElements(Predicate<? super T> predicate) {
        return new DefaultListProvider<>(this.elementProviders, List.of(predicate));
    }

    @Override
    public List<T> get() {
        return this.elementProviders.stream()
                .map(Provider::get)
                .toList();
    }

}
