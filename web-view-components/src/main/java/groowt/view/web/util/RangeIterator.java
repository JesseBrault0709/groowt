package groowt.view.web.util;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface RangeIterator<T> extends Iterator<T> {

    interface NextSupplier<T> {
        @Nullable T next(int index);
    }

    interface StartIndexFunction<T> {
        int getStartIndex(T start);
    }

    int currentIndex();

}
