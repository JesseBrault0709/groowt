package groowt.util.fp.property;

import groowt.util.fp.provider.ListProvider;
import groowt.util.fp.provider.Provider;

import java.util.Collection;

public interface ListProperty<T> extends ListProvider<T> {
    void addElement(T element);
    void addProvider(Provider<? extends T> elementProvider);
    void addAllElements(Collection<? extends T> elements);
    void addAllProviders(Collection<? extends Provider<? extends T>> elementProviders);
}
