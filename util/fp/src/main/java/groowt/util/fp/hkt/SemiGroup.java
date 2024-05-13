package groowt.util.fp.hkt;

import java.util.function.BinaryOperator;

public final class SemiGroup<T> {

    private final BinaryOperator<T> concat;

    public SemiGroup(BinaryOperator<T> concat) {
        this.concat = concat;
    }

    public T concat(T left, T right) {
        return this.concat.apply(left, right);
    }

}
