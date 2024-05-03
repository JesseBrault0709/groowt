package groowt.gradle.antlr;

import org.gradle.api.provider.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public final class NullableProviderList<T> {

    private sealed interface Element<T> extends Iterable<T> permits BareElement, ElementProvider, CollectionProvider {
        boolean isPresent();
    }

    private static final class BareElement<T> implements Element<T> {

        private final T element;

        public BareElement(T element) {
            this.element = element;
        }

        @Override
        public boolean isPresent() {
            return this.element != null;
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                private boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return this.hasNext;
                }

                @Override
                public T next() {
                    if (!this.hasNext) throw new IllegalStateException();
                    this.hasNext = false;
                    return BareElement.this.element;
                }
            };
        }

    }

    private static final class ElementProvider<T> implements Element<T> {

        private final Provider<T> provider;

        public ElementProvider(Provider<T> provider) {
            this.provider = provider;
        }

        @Override
        public boolean isPresent() {
            return this.provider.isPresent();
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return this.hasNext;
                }

                @Override
                public T next() {
                    if (!this.hasNext) throw new IllegalStateException();
                    this.hasNext = false;
                    return ElementProvider.this.provider.get();
                }
            };
        }

    }

    private static final class CollectionProvider<T> implements Element<T> {

        private final Provider<Collection<T>> collectionProvider;

        public CollectionProvider(Provider<Collection<T>> collectionProvider) {
            this.collectionProvider = collectionProvider;
        }

        @Override
        public boolean isPresent() {
            return this.collectionProvider.isPresent();
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return this.collectionProvider.get().iterator();
        }

    }

    private final List<Element<T>> elements = new ArrayList<>();

    public void addElement(@Nullable T element) {
        this.elements.add(new BareElement<>(element));
    }

    public void addProvider(Provider<T> elementProvider) {
        this.elements.add(new ElementProvider<>(elementProvider));
    }

    public void addCollectionProvider(Provider<Collection<T>> collectionProvider) {
        this.elements.add(new CollectionProvider<>(collectionProvider));
    }

    public void addAllElements(Collection<T> elements) {
        for (final T element : elements) {
            this.elements.add(new BareElement<>(element));
        }
    }

    public void addAllProviders(Collection<Provider<T>> providers) {
        for (final Provider<T> provider : providers) {
            this.elements.add(new ElementProvider<>(provider));
        }
    }

    public void addAllCollectionProviders(Collection<Provider<Collection<T>>> collectionProviders) {
        for (final Provider<Collection<T>> collectionProvider : collectionProviders) {
            this.elements.add(new CollectionProvider<>(collectionProvider));
        }
    }

    public List<T> getElements() {
        return this.getElements(null);
    }

    public List<T> getElements(@Nullable Supplier<T> onNullElement) {
        final List<T> result = new ArrayList<>();
        for (final Element<T> element : this.elements) {
            if (element.isPresent()) {
                for (final T t : element) {
                    result.add(t);
                }
            } else if (onNullElement != null) {
                result.add(onNullElement.get());
            }
        }
        return result;
    }

}
