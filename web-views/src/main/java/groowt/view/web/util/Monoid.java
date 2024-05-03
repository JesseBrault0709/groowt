package groowt.view.web.util;

public final class Monoid<T> {

    private final SemiGroup<T> semiGroup;
    private final T empty;

    public Monoid(SemiGroup<T> semiGroup, T empty) {
        this.semiGroup = semiGroup;
        this.empty = empty;
    }

    public T concat(T left, T right) {
        return this.semiGroup.concat(left, right);
    }

    public T empty() {
        return this.empty;
    }

}
