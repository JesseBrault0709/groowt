package groowt.util.fp.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DefaultListProvider<T> implements ListProvider<T> {

    public static <T> ListProvider<T> ofElements(Class<T> elementType, List<? extends T> elements) {
        return new DefaultListProvider<>(
                elementType,
                elements.stream()
                        .<Provider<T>>map(DefaultProvider::of)
                        .toList()
        );
    }

    public static <T> ListProvider<T> ofElementProviders(
            Class<T> elementType,
            List<? extends Provider<T>> elementProviders
    ) {
        return new DefaultListProvider<>(elementType, elementProviders);
    }

    private final Class<T> elementType;
    private final List<Provider<T>> elementProviders;

    private DefaultListProvider(Class<T> elementType, List<? extends Provider<T>> elementProviders) {
        this.elementType = elementType;
        this.elementProviders = new ArrayList<>(elementProviders);
    }

    @Override
    public Class<T> getType() {
        return this.elementType;
    }

    @Override
    public <U> ListProvider<U> map(Class<U> targetType, Function<? super T, ? extends U> mapper) {
        final List<Provider<U>> uProviders = this.elementProviders.stream()
                .map(elementProvider -> elementProvider.map(targetType, mapper))
                .toList();
        return new DefaultListProvider<>(targetType, uProviders);
    }

    @Override
    public <U> ListProvider<U> flatMap(
            Class<U> targetType,
            Function<? super T, ? extends Provider<? extends U>> flatMapper
    ) {
        final List<Provider<U>> uProviders = this.elementProviders.stream()
                .map(elementProvider -> elementProvider.flatMap(targetType, flatMapper))
                .toList();
        return new DefaultListProvider<>(targetType, uProviders);
    }

    @Override
    public <U extends T> ListProvider<U> withType(Class<U> desiredType) {
        return new DefaultListProvider<>(desiredType, this.elementProviders.stream()
                .filter(elementProvider -> desiredType.isAssignableFrom(elementProvider.getType()))
                .map(elementProvider -> elementProvider.map(desiredType, desiredType::cast))
                .toList()
        );
    }

    @Override
    public List<T> get() {
        final List<? extends T> filtered = this.elementProviders.stream()
                .map(Provider::get)
                .toList();
        return new ArrayList<>(filtered);
    }

}
