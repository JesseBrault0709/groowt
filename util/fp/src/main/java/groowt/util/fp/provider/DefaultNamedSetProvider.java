package groowt.util.fp.provider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class DefaultNamedSetProvider<T> extends DefaultSetProvider<T> implements NamedSetProvider<T> {

    public static <T> NamedSetProvider<T> ofElementsAndNames(Class<T> type, Map<String, T> namesAndElements) {
        return new DefaultNamedSetProvider<>(type, namesAndElements.entrySet().stream()
                .map(entry -> new DefaultNamedProvider<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet())
        );
    }

    public static <T> NamedSetProvider<T> ofNamedProviders(Class<T> type, Set<? extends NamedProvider<T>> providers) {
        return new DefaultNamedSetProvider<>(type, providers);
    }

    protected DefaultNamedSetProvider(Class<T> type, Set<? extends NamedProvider<T>> elementProviders) {
        super(type, elementProviders);
    }

    @Override
    public NamedSetProvider<T> withNames(Predicate<? super String> namePredicate) {
        return new DefaultNamedSetProvider<>(this.getType(), this.getProviders().stream()
                .map(provider -> (NamedProvider<T>) provider)
                .filter(namedProvider -> namePredicate.test(namedProvider.getName()))
                .collect(Collectors.toSet())
        );
    }

    @Override
    public NamedProvider<T> withName(String name) {
        return this.getProviders().stream()
                .map(provider -> (NamedProvider<T>) provider)
                .filter(namedProvider -> name.equals(namedProvider.getName()))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("There is no NamedProvider present with name " + name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NamedSetProvider<T> zipWithNames(NamedSetProvider<? extends T> other) {
        final Set<NamedProvider<T>> combined = new HashSet<>();
        this.getProviders().forEach(provider -> combined.add((NamedProvider<T>) provider));
        other.getProviders().forEach(provider -> combined.add((NamedProvider<T>) provider));
        return new DefaultNamedSetProvider<>(this.getType(), combined);
    }

}
