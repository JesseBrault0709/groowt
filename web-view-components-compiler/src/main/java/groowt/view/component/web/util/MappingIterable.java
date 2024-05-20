package groowt.view.component.web.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

public final class MappingIterable<T, U> implements Iterable<U> {

    private final Iterable<T> source;
    private final Function<T, U> mapper;

    public MappingIterable(Iterable<T> source, Function<T, U> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @NotNull
    @Override
    public Iterator<U> iterator() {
        return new MappingIterator<>(this.source.iterator(), this.mapper);
    }

}
