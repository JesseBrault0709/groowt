package groowt.view.web.transpile;

import jakarta.inject.Inject;

public class DefaultTranspilerConfiguration implements TranspilerConfiguration {

    private final AppendOrAddStatementFactory appendOrAddStatementFactory = new DefaultAppendOrAddStatementFactory();
    private final BodyTranspiler bodyTranspiler;

    @Inject
    public DefaultTranspilerConfiguration() {
        final var positionSetter = new SimplePositionSetter();
        final var jStringTranspiler = new DefaultJStringTranspiler(positionSetter);
        final var gStringTranspiler = new DefaultGStringTranspiler(positionSetter, jStringTranspiler);
        final var componentTranspiler = new DefaultComponentTranspiler();
        final var valueNodeTranspiler = new DefaultValueNodeTranspiler(componentTranspiler);
        
        this.bodyTranspiler = new DefaultBodyTranspiler(gStringTranspiler, jStringTranspiler, componentTranspiler);

        componentTranspiler.setBodyTranspiler(this.bodyTranspiler);
        componentTranspiler.setValueNodeTranspiler(valueNodeTranspiler);
        componentTranspiler.setAppendOrAddStatementFactory(this.appendOrAddStatementFactory);
    }

    @Override
    public BodyTranspiler getBodyTranspiler() {
        return this.bodyTranspiler;
    }

    @Override
    public AppendOrAddStatementFactory getAppendOrAddStatementFactory() {
        return this.appendOrAddStatementFactory;
    }

}
