package groowt.view.component.web.util;

import org.jetbrains.annotations.NotNull;

public class EmptyRange<T> implements Range<T> {

    private static final EmptyRange<Object> INSTANCE = new EmptyRange<>();

    @SuppressWarnings("unchecked")
    public static <T> EmptyRange<T> get() {
        return (EmptyRange<T>) INSTANCE;
    }

    protected EmptyRange() {}

    @Override
    public boolean isInRange(@NotNull T index) {
        return false;
    }

    @Override
    public @NotNull T getStart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RangeIterator<T> rangeIterator(RangeIterator.NextSupplier<T> nextSupplier) {
        return new RangeIterator<T>() {

            @Override
            public int currentIndex() {
                throw new UnsupportedOperationException(
                        "Cannot currentIndex() on an EmptyRange's RangeIterator. Did you call hasNext() first?"
                );
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new UnsupportedOperationException(
                        "Cannot next() on an EmptyRange's RangeIterator. Did you call hasNext() first?"
                );
            }

        };
    }

}
