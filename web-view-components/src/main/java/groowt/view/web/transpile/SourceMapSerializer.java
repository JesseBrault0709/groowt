package groowt.view.web.transpile;

@FunctionalInterface
public interface SourceMapSerializer {
    String serialize(SourceMap sourceMap);
}
