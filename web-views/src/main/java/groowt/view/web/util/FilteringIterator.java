package groowt.view.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

final class FilteringIterator<T> implements Iterator<T> {

    public enum OnFilterFail {STOP, CONTINUE_UNTIL_SUCCESS}

    private final List<T> elements = new ArrayList<>();
    private final Predicate<? super T> filter;
    private final OnFilterFail onFilterFail;
    private final Range<Integer> range;
    private int currentPosition;
    private int lastFetchedPosition = -1;
    private T fetched = null;

    public FilteringIterator(
            List<? extends T> elements,
            Predicate<? super T> filter,
            OnFilterFail onFilterFail,
            Range<Integer> range
    ) {
        this.elements.addAll(elements);
        this.filter = filter;
        this.onFilterFail = onFilterFail;
        this.range = range;
        this.currentPosition = this.range.getStart();
    }

    private void fetchAt(int index) {
        if (index != this.lastFetchedPosition) {
            if (this.range.isInRange(index) && index < this.elements.size()) {
                final T potentialFetched = this.elements.get(index);
                if (this.filter.test(potentialFetched)) {
                    this.fetched = potentialFetched;
                    this.lastFetchedPosition = index;
                } else {
                    switch (this.onFilterFail) {
                        case STOP -> {
                            this.fetched = null;
                        }
                        case CONTINUE_UNTIL_SUCCESS -> {
                            this.fetchAt(index + 1);
                        }
                    }
                }
            } else {
                this.fetched = null;
            }
        }
    }

    @Override
    public boolean hasNext() {
        this.fetchAt(this.currentPosition);
        this.currentPosition = this.lastFetchedPosition;
        return this.fetched != null;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            this.currentPosition++;
            return this.fetched;
        } else {
            throw new IndexOutOfBoundsException("Cannot next() when hasNext is false.");
        }
    }

}
