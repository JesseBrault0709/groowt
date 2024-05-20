package groowt.view.web.util;

import java.util.Objects;

public abstract class AbstractClosedRange<T> implements ClosedRange<T> {

    protected final T start;
    protected final T end;
    protected final boolean inclusiveStart;
    protected final boolean inclusiveEnd;
    protected final RangeIterator.StartIndexFunction<T> startIndexFunction;

    protected AbstractClosedRange(
            T start,
            T end,
            boolean inclusiveEnd,
            boolean inclusiveStart,
            RangeIterator.StartIndexFunction<T> startIndexFunction
    ) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        this.inclusiveStart = inclusiveStart;
        this.inclusiveEnd = inclusiveEnd;
        this.startIndexFunction = startIndexFunction;
    }

    @Override
    public T getStart() {
        return this.start;
    }

    @Override
    public T getEnd() {
        return this.end;
    }

    @Override
    public boolean isInclusiveStart() {
        return this.inclusiveStart;
    }

    @Override
    public boolean isInclusiveEnd() {
        return this.inclusiveEnd;
    }

    protected abstract int compare(T left, T right);

    private boolean compare(T item) {
        Objects.requireNonNull(item);
        return this.inclusiveStart ? this.compare(item, this.start) >= 0 : this.compare(item, this.start) > 0;
    }

    private boolean checkEnd(T item) {
        Objects.requireNonNull(item);
        return this.inclusiveEnd ? this.compare(item, this.end) <= 0 : this.compare(item, this.end) < 0;
    }

    @Override
    public boolean isInRange(T item) {
        return this.compare(item) && this.checkEnd(item);
    }

    @Override
    public RangeIterator<T> rangeIterator(RangeIterator.NextSupplier<T> nextSupplier) {
        return new SimpleRangeIterator<>(this, nextSupplier, this.startIndexFunction.getStartIndex(this.start));
    }

}
