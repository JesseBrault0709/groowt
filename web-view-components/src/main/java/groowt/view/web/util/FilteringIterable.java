package groowt.view.web.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class FilteringIterable<T> implements Iterable<T> {

    public static <T> FilteringIterable<T> stoppingOnFail(List<? extends T> elements, Predicate<? super T> filter) {
        return stoppingOnFail(elements, filter, ClosedRange.intRange(0, elements.size()));
    }

    public static <T> FilteringIterable<T> stoppingOnFail(
            List<? extends T> elements,
            Predicate<? super T> filter,
            Range<Integer> range
    ) {
        return new FilteringIterable<>(elements, filter, FilteringIterator.OnFilterFail.STOP, range);
    }

    public static <T> FilteringIterable<T> continuingUntilSuccess(
            List<? extends T> elements,
            Predicate<? super T> filter
    ) {
        return continuingUntilSuccess(elements, filter, ClosedRange.intRange(0, elements.size()));
    }

    public static <T> FilteringIterable<T> continuingUntilSuccess(
            List<? extends T> elements,
            Predicate<? super T> filter,
            Range<Integer> range
    ) {
        return new FilteringIterable<>(elements, filter, FilteringIterator.OnFilterFail.CONTINUE_UNTIL_SUCCESS, range);
    }

    private final List<T> elements = new ArrayList<>();
    private final Predicate<? super T> filter;
    private final FilteringIterator.OnFilterFail onFilterFail;
    private final Range<Integer> range;

    private FilteringIterable(
            List<? extends T> elements,
            Predicate<? super T> filter,
            FilteringIterator.OnFilterFail onFilterFail,
            Range<Integer> range
    ) {
        this.elements.addAll(elements);
        this.filter = filter;
        this.onFilterFail = onFilterFail;
        this.range = range;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new FilteringIterator<>(this.elements, this.filter, this.onFilterFail, this.range);
    }

}
