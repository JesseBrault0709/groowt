package groowt.view.component.web.transpile;

@FunctionalInterface
public interface SourceMapSerializer {
    String serialize(SourceMap sourceMap);
}
