package groowt.util.di;

public interface KeyHolder<B extends KeyBinder<K>, K, T> {
    Class<B> binderType();
    Class<T> type();
    K key();
}
