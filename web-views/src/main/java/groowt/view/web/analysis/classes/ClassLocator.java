package groowt.view.web.analysis.classes;

public sealed interface ClassLocator permits ClassLoaderClassLocator, PreambleAwareClassLocator {
    boolean hasClassForFQN(String name);
}