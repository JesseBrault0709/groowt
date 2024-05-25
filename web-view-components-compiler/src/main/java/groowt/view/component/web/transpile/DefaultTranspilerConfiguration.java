package groowt.view.component.web.transpile;

import groovy.lang.Tuple3;
import groowt.util.fp.provider.DefaultProvider;
import groowt.view.component.web.transpile.resolve.ComponentClassNodeResolver;
import org.codehaus.groovy.ast.ClassNode;

import java.util.Map;
import java.util.Set;

import static groowt.view.component.web.transpile.TranspilerUtil.*;

public class DefaultTranspilerConfiguration implements TranspilerConfiguration {

    private final PositionSetter positionSetter;
    private final AppendOrAddStatementFactory appendOrAddStatementFactory = new DefaultAppendOrAddStatementFactory();
    private final BodyTranspiler bodyTranspiler;
    private final ValueNodeTranspiler valueNodeTranspiler;

    public DefaultTranspilerConfiguration(ComponentClassNodeResolver classNodeResolver) {
        this.positionSetter = new SimplePositionSetter();
        final var jStringTranspiler = new DefaultJStringTranspiler(this.positionSetter);
        final var gStringTranspiler = new DefaultGStringTranspiler(this.positionSetter, jStringTranspiler);
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
    public PositionSetter getPositionSetter() {
        return this.positionSetter;
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

    @Override
    public Map<String, ClassNode> getImports() {
        return Map.of(
                COMPONENT_TEMPLATE.getNameWithoutPackage(), COMPONENT_TEMPLATE,
                COMPONENT_CONTEXT_TYPE.getNameWithoutPackage(), COMPONENT_CONTEXT_TYPE
        );
    }

    @Override
    public Set<String> getStarImports() {
        return Set.of(
                GROOWT_VIEW_COMPONENT_WEB + ".lib",
                "groowt.view.component.runtime",
                GROOWT_VIEW_COMPONENT_WEB + ".runtime"
        );
    }

    @Override
    public Set<Tuple3<ClassNode, String, String>> getStaticImports() {
        return Set.of();
    }

    @Override
    public Map<String, ClassNode> getStaticStarImports() {
        return Map.of();
    }

    @Override
    public ClassNode getRenderContextImplementation() {
        return DEFAULT_RENDER_CONTEXT_IMPLEMENTATION;
    }

}
