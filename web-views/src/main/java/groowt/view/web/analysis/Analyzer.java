package groowt.view.web.analysis;

import java.util.List;

public sealed interface Analyzer<T, E> permits ParseTreeAnalyzer, AstAnalyzer {
    List<E> analyze(T t);
}