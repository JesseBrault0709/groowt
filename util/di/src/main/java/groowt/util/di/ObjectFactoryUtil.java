package groowt.util.di;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ObjectFactoryUtil {

    public static Class<?>[] toTypes(Object... objects) {
        final Class<?>[] types = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            final Object o = objects[i];
            if (o != null) {
                types[i] = o.getClass();
            } else {
                types[i] = Object.class;
            }
        }
        return types;
    }

    private ObjectFactoryUtil() {}

}
