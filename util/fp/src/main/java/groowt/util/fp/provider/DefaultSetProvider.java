package groowt.util.fp.provider;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultSetProvider<T> implements SetProvider<T> {

    public static <T> SetProvider<T> ofElements(Class<T> type, Set<? extends T> elements) {
        return new DefaultSetProvider<>(type, elements.stream()
                .<Provider<T>>map(DefaultProvider::of)
                .collect(Collectors.toSet())
        );
    }

    public static <T> SetProvider<T> ofElementProviders(Class<T> type, Set<? extends Provider<T>> providers) {
        return new DefaultSetProvider<>(type, providers);
    }

    private final Class<T> type;
    private final Set<Provider<T>> elementProviders;

    protected DefaultSetProvider(Class<T> type, Set<? extends Provider<T>> elementProviders) {
        this.type = type;
        this.elementProviders = new HashSet<>(elementProviders);
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public Set<Provider<T>> getProviders() {
        return this.elementProviders;
    }

    @Override
    public <U> SetProvider<U> map(Class<U> targetType, Function<? super T, ? extends U> mapper) {
        return new DefaultSetProvider<>(targetType, this.elementProviders.stream()
                .map(elementProvider -> elementProvider.map(targetType, mapper))
                .collect(Collectors.toSet())
        );
    }

    @Override
    public <U> SetProvider<U> flatMap(Class<U> targetType, Function<? super T, ? extends Provider<U>> mapper) {
        return new DefaultSetProvider<>(targetType, this.elementProviders.stream()
                .map(elementProvider -> elementProvider.flatMap(targetType, mapper))
                .collect(Collectors.toSet())
        );
    }

    @Override
    public <U> SetProvider<U> withType(Class<U> desiredType) {
        return new DefaultSetProvider<>(desiredType, this.elementProviders.stream()
                .filter(elementProvider -> desiredType.isAssignableFrom(elementProvider.getType()))
                .map(elementProvider -> elementProvider.map(desiredType, desiredType::cast))
                .collect(Collectors.toSet())
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetProvider<T> zip(SetProvider<? extends T> other) {
        final Set<Provider<T>> combined = new HashSet<>();
        combined.addAll(this.elementProviders);
        other.getProviders().forEach(provider -> combined.add((Provider<T>) provider));
        return new DefaultSetProvider<>(this.getType(), combined);
    }

    @Override
    public Set<T> get() {
        return this.elementProviders.stream().map(Provider::get).collect(Collectors.toSet());
    }

}
