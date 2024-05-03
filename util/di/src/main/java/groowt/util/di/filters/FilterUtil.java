package groowt.util.di.filters;

public final class FilterUtil {

    private FilterUtil() {}

    public static boolean isAssignableToAnyOf(Class<?> subject, Class<?>[] tests) {
        for (final var test : tests) {
            if (test.isAssignableFrom(subject)) {
                return true;
            }
        }
        return false;
    }

}
