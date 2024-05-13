package groowt.util.fp.provider;

import groowt.util.fp.hkt.SemiGroup;

public interface NamedProvider<T> extends Provider<T> {

    String getName();

    default NamedProvider<T> zipWithNames(
            SemiGroup<T> tSemiGroup,
            SemiGroup<String> nameSemiGroup,
            NamedProvider<? extends T> other
    ) {
        return new DefaultNamedProvider<>(
                nameSemiGroup.concat(this.getName(), other.getName()),
                Provider.ofLazy(() -> tSemiGroup.concat(this.get(), other.get()))
        );
    }

}
