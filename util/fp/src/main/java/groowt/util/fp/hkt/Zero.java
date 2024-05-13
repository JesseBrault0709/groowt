package groowt.util.fp.hkt;

import java.util.Objects;

public final class Zero<T> {

    private final T empty;

    public Zero(T empty) {
        this.empty = Objects.requireNonNull(empty);
    }

    public T getEmpty() {
        return this.empty;
    }

}
