package groowt.util.fp.property;

import groowt.util.fp.provider.DefaultListProvider;
import groowt.util.fp.provider.DefaultProvider;
import groowt.util.fp.provider.ListProvider;
import groowt.util.fp.provider.Provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class DefaultListProperty<T> implements ListProperty<T> {

    public static <T> ListProperty<T> ofType(Class<T> type) {
        return new DefaultListProperty<>(type);
    }

    private final Class<T> type;
    private final List<Provider<T>> elementProviders = new ArrayList<>();

    protected DefaultListProperty(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public void addElement(T element) {
        this.elementProviders.add(DefaultProvider.of(element));
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
    public <U> ListProvider<U> map(Class<U> targetType, Function<? super T, ? extends U> mapper) {
        return DefaultListProvider.ofElementProviders(targetType, this.elementProviders.stream()
                .map(elementProvider -> elementProvider.map(targetType, mapper))
                .toList()
        );
    }

    @Override
    public <U> ListProvider<U> flatMap(
            Class<U> targetType,
            Function<? super T, ? extends Provider<? extends U>> flatMapper
    ) {
        return DefaultListProvider.ofElementProviders(targetType, this.elementProviders.stream()
                .map(elementProvider -> elementProvider.flatMap(targetType, flatMapper))
                .toList()
        );
    }

    @Override
    public <U extends T> ListProvider<U> withType(Class<U> desiredType) {
        return DefaultListProvider.ofElementProviders(desiredType, this.elementProviders.stream()
                .filter(elementProvider -> desiredType.isAssignableFrom(elementProvider.getType()))
                .map(elementProvider -> elementProvider.map(desiredType, desiredType::cast))
                .toList()
        );
    }

    @Override
    public List<T> get() {
        return this.elementProviders.stream()
                .map(Provider::get)
                .toList();
    }

}
