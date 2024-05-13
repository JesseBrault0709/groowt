package groowt.util.fp.hkt;

import java.util.Objects;

public final class Monoid<T> {

    private final SemiGroup<T> semiGroup;
    private final Zero<T> zero;

    public Monoid(SemiGroup<T> semiGroup, Zero<T> zero) {
        this.semiGroup = Objects.requireNonNull(semiGroup);
        this.zero = Objects.requireNonNull(zero);
    }

    public T concat(T left, T right) {
        return this.semiGroup.concat(left, right);
    }

    public T empty() {
        return this.zero.getEmpty();
    }

}
