package groowt.view.component.web.util;

public interface Range<T> {
    boolean isInRange(T item);
    T getStart();
    RangeIterator<T> rangeIterator(RangeIterator.NextSupplier<T> nextSupplier);
}
