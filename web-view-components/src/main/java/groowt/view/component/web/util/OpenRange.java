package groowt.view.component.web.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OpenRange<T extends Comparable<T>> implements Range<T> {

    private final T start;
    private final RangeIterator.StartIndexFunction<T> startIndexFunction;

    public OpenRange(T start, RangeIterator.StartIndexFunction<T> startIndexFunction) {
        this.start = start;
        this.startIndexFunction = startIndexFunction;
    }

    @Override
    public @NotNull T getStart() {
        return this.start;
    }

    @Override
    public boolean isInRange(@NotNull T item) {
        return Objects.requireNonNull(item).compareTo(this.start) >= 0;
    }

    @Override
    public RangeIterator<T> rangeIterator(RangeIterator.NextSupplier<T> nextSupplier) {
        return new SimpleRangeIterator<>(this, nextSupplier, this.startIndexFunction.getStartIndex(this.start));
    }

}
