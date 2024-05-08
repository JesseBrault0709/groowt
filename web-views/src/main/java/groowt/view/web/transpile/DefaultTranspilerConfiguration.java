package groowt.view.web.transpile;

import groowt.view.web.transpile.resolve.ComponentClassNodeResolver;
import groowt.view.web.util.Provider;

public class DefaultTranspilerConfiguration implements TranspilerConfiguration {

    private final AppendOrAddStatementFactory appendOrAddStatementFactory = new DefaultAppendOrAddStatementFactory();
    private final BodyTranspiler bodyTranspiler;
    private final ValueNodeTranspiler valueNodeTranspiler;

    public DefaultTranspilerConfiguration(ComponentClassNodeResolver classNodeResolver) {
        final var positionSetter = new SimplePositionSetter();
        final var jStringTranspiler = new DefaultJStringTranspiler(positionSetter);
        final var gStringTranspiler = new DefaultGStringTranspiler(positionSetter, jStringTranspiler);
        final var componentTranspiler = new DefaultComponentTranspiler(
                Provider.of(this.appendOrAddStatementFactory),
                Provider.of(classNodeResolver),
                Provider.ofLazy(this::getValueNodeTranspiler),
                Provider.ofLazy(this::getBodyTranspiler)
        );
        this.valueNodeTranspiler = new DefaultValueNodeTranspiler(componentTranspiler);
        this.bodyTranspiler = new DefaultBodyTranspiler(gStringTranspiler, jStringTranspiler, componentTranspiler);
    }

    @Override
    public BodyTranspiler getBodyTranspiler() {
        return this.bodyTranspiler;
    }

    @Override
    public AppendOrAddStatementFactory getAppendOrAddStatementFactory() {
        return this.appendOrAddStatementFactory;
    }

    protected ValueNodeTranspiler getValueNodeTranspiler() {
        return this.valueNodeTranspiler;
    }

}
