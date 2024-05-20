package groowt.view.component.web.util;

public interface ClosedRange<T> extends Range<T> {

    static ClosedRange<Integer> intRange(int startAndEnd) {
        return intRange(startAndEnd, startAndEnd, false, true);
    }

    static ClosedRange<Integer> intRange(int start, int end) {
        return intRange(start, end, false, true);
    }

    static ClosedRange<Integer> intRange(int start, int end, boolean inclusiveEnd) {
        return intRange(start, end, inclusiveEnd, true);
    }

    static ClosedRange<Integer> intRange(int start, int end, boolean inclusiveEnd, boolean inclusiveStart) {
        return new ComparableClosedRange<>(start, end, inclusiveEnd, inclusiveStart, _start -> _start);
    }

    T getEnd();
    boolean isInclusiveStart();
    boolean isInclusiveEnd();

}
