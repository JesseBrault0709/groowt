package groowt.view.web.util;

import java.util.Comparator;

public class ComparatorClosedRange<T> extends AbstractClosedRange<T> {

    private final Comparator<T> comparator;

    public ComparatorClosedRange(
            T start,
            T end,
            boolean inclusiveEnd,
            boolean inclusiveStart,
            Comparator<T> comparator,
            RangeIterator.StartIndexFunction<T> startIndexFunction
    ) {
        super(start, end, inclusiveEnd, inclusiveStart, startIndexFunction);
        this.comparator = comparator;
    }

    @Override
    protected int compare(T left, T right) {
        return this.comparator.compare(left, right);
    }

}
