package groowt.view.component.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ComponentFactoryUtil {

    public static final Class<?>[] EMPTY_CLASSES = new Class[0];

    public static Object[] flatten(Object... args) {
        if (args.length == 0) {
            return args;
        } else {
            final List<Object> result = new ArrayList<>(args.length);
            for (final var arg : args) {
                if (arg instanceof Object[] arr && arr.length > 0) {
                    result.addAll(Arrays.asList(arr));
                } else {
                    result.add(arg);
                }
            }
            return result.toArray(Object[]::new);
        }
    }

    public static Class<?>[] asTypes(Object[] args) {
        if (args.length == 0) {
            return EMPTY_CLASSES;
        }
        final Class<?>[] result = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            if (arg instanceof Class<?> argAsClass) {
                result[i] = argAsClass;
            } else {
                result[i] = arg.getClass();
            }
        }
        return result;
    }

    private ComponentFactoryUtil() {}

}
