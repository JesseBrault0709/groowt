package groowt.view.web.transpile;

public interface TranspilerConfiguration {
    PreambleTranspiler getPreambleTranspiler();
    BodyTranspiler getBodyTranspiler();
    OutStatementFactory getOutStatementFactory();
}
