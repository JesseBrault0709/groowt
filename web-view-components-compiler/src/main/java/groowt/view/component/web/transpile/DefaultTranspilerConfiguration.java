package groowt.view.component.web.transpile;

import groowt.util.fp.provider.DefaultProvider;
import groowt.view.component.web.transpile.resolve.ComponentClassNodeResolver;

public class DefaultTranspilerConfiguration implements TranspilerConfiguration {

    private final AppendOrAddStatementFactory appendOrAddStatementFactory = new DefaultAppendOrAddStatementFactory();
    private final BodyTranspiler bodyTranspiler;
    private final ValueNodeTranspiler valueNodeTranspiler;

    public DefaultTranspilerConfiguration(ComponentClassNodeResolver classNodeResolver) {
        final var positionSetter = new SimplePositionSetter();
        final var jStringTranspiler = new DefaultJStringTranspiler(positionSetter);
        final var gStringTranspiler = new DefaultGStringTranspiler(positionSetter, jStringTranspiler);
        final var componentTranspiler = new DefaultComponentTranspiler(
                DefaultProvider.of(this.appendOrAddStatementFactory),
                DefaultProvider.of(classNodeResolver),
                DefaultProvider.ofLazy(ValueNodeTranspiler.class, this::getValueNodeTranspiler),
                DefaultProvider.ofLazy(BodyTranspiler.class, this::getBodyTranspiler)
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
