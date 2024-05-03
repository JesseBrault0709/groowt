package groowt.util.di;

public record SimpleKeyHolder<B extends KeyBinder<K>, K, T>(Class<B> binderType, Class<T> type, K key)
        implements KeyHolder<B, K, T> {}
