package groowt.util.fp.property;

import groowt.util.fp.provider.ListProvider;
import groowt.util.fp.provider.Provider;

import java.util.Collection;
import java.util.Objects;

public interface ListProperty<T> extends ListProvider<T> {

    void addElement(T element);
    void addProvider(Provider<? extends T> elementProvider);
    void addAllElements(Collection<? extends T> elements);
    void addAllProviders(Collection<? extends Provider<? extends T>> elementProviders);

    @SuppressWarnings("unchecked")
    default void leftShift(Object object) {
        Objects.requireNonNull(object);
        if (object instanceof Provider<?> provider) {
            if (!this.getType().isAssignableFrom(provider.getType())) {
                throw new IllegalArgumentException(
                        "The type of the given Provider (" + provider.getType().getName() + ") is not compatible with" +
                                "the type of this ListProperty (" + this.getType().getName() + ")."
                );
            }
            this.addProvider((Provider<? extends T>) provider);
        } else if (this.getType().isAssignableFrom(object.getClass())) {
            this.addElement((T) object);
        } else {
            throw new IllegalArgumentException("The type of the given object (" + object.getClass().getName() +
                    ") is not compatible with the type of this ListProperty (" + this.getType().getName() + ")."
            );
        }
    }

}
