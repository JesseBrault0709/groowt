package groowt.util.extensible;

public interface ExtensionContainer<E, F> extends ExtensionAware<E> {
    F getExtensionFactory();
}
