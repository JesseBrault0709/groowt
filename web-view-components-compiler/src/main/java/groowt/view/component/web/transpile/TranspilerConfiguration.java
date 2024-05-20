package groowt.view.component.web.transpile;

public interface TranspilerConfiguration {
    BodyTranspiler getBodyTranspiler();
    AppendOrAddStatementFactory getAppendOrAddStatementFactory();
}
