package groowt.view.web.transpile;

import jakarta.inject.Inject;

public class DefaultTranspilerConfiguration implements TranspilerConfiguration {

    private final OutStatementFactory outStatementFactory = new SimpleOutStatementFactory();
    private final PreambleTranspiler preambleTranspiler = new DefaultPreambleTranspiler();
    private final BodyTranspiler bodyTranspiler;

    @Inject
    public DefaultTranspilerConfiguration() {
        final var positionSetter = new SimplePositionSetter();
        final var jStringTranspiler = new DefaultJStringTranspiler(positionSetter);
        final var gStringTranspiler = new DefaultGStringTranspiler(positionSetter, jStringTranspiler);
        final var componentTranspiler = new DefaultComponentTranspiler();
        this.bodyTranspiler = new DefaultBodyTranspiler(gStringTranspiler, jStringTranspiler, componentTranspiler);
        componentTranspiler.setBodyTranspiler(this.bodyTranspiler);
        final var valueNodeTranspiler = new DefaultValueNodeTranspiler(componentTranspiler);
        componentTranspiler.setValueNodeTranspiler(valueNodeTranspiler);
    }

    @Override
    public PreambleTranspiler getPreambleTranspiler() {
        return this.preambleTranspiler;
    }

    @Override
    public BodyTranspiler getBodyTranspiler() {
        return this.bodyTranspiler;
    }

    @Override
    public OutStatementFactory getOutStatementFactory() {
        return this.outStatementFactory;
    }

}
