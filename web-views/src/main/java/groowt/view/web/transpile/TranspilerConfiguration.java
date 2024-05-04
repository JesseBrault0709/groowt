package groowt.view.web.transpile;

public interface TranspilerConfiguration {
    BodyTranspiler getBodyTranspiler();
    AppendOrAddStatementFactory getAppendOrAddStatementFactory();
}
