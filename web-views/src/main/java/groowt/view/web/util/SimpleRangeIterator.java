package groowt.view.web.util;

import org.jetbrains.annotations.Nullable;

final class SimpleRangeIterator<T> implements RangeIterator<T> {

    private final Range<T> range;
    private final NextSupplier<T> nextSupplier;
    private @Nullable T next;
    private int currentIndex;

    public SimpleRangeIterator(Range<T> range, NextSupplier<T> nextSupplier, int startIndex) {
        this.range = range;
        this.nextSupplier = nextSupplier;
        this.currentIndex = startIndex;
    }

    @Override
    public int currentIndex() {
        return this.currentIndex;
    }

    @Override
    public boolean hasNext() {
        if (this.next == null) {
            this.next = this.nextSupplier.next(this.currentIndex);
        }
        return this.next != null && this.range.isInRange(next);
    }

    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new IndexOutOfBoundsException("Cannot next() when hasNext() is false.");
        }
        this.currentIndex++;
        final T result = this.next;
        this.next = null;
        return result;
    }

}
