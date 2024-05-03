package groowt.util.di;

public interface NamedRegistryExtension extends RegistryExtension, KeyBinder<String>, QualifierHandlerContainer {

    static <T> KeyHolder<NamedRegistryExtension, String, T> named(String name, Class<T> type) {
        return new SimpleKeyHolder<>(NamedRegistryExtension.class, type, name);
    }

}
