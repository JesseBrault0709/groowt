package groowt.view.web.util;

import java.util.Iterator;
import java.util.function.Function;

public final class MappingIterator<T, U> implements Iterator<U> {

    private final Iterator<T> source;
    private final Function<T, U> mapper;

    public MappingIterator(Iterator<T> source, Function<T, U> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return this.source.hasNext();
    }

    @Override
    public U next() {
        if (this.hasNext()) {
            return this.mapper.apply(this.source.next());
        } else {
            throw new IndexOutOfBoundsException("Cannot next() when hasNext() is false.");
        }
    }

}
