package groowt.view.component.web.util;

public final class ComparableClosedRange<T extends Comparable<T>> extends AbstractClosedRange<T> {

    public ComparableClosedRange(
            T start,
            T end,
            boolean inclusiveEnd,
            boolean inclusiveStart,
            RangeIterator.StartIndexFunction<T> startIndexFunction
    ) {
        super(start, end, inclusiveEnd, inclusiveStart, startIndexFunction);
    }

    @Override
    protected int compare(T left, T right) {
        return left.compareTo(right);
    }

}
