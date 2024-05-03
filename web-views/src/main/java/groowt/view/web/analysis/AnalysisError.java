package groowt.view.web.analysis;

public interface AnalysisError<T> {
    T subject();
    String message();
}