package groowt.view.component.web.transpile;

import groovy.lang.Tuple3;
import groowt.view.component.web.transpile.resolve.ComponentClassNodeResolver;
import org.codehaus.groovy.ast.ClassNode;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static groowt.view.component.web.transpile.TranspilerUtil.*;

public class SimpleTranspilerConfiguration implements TranspilerConfiguration {

    public static TranspilerConfiguration withDefaults(ComponentClassNodeResolver componentClassNodeResolver) {
        final var c = new SimpleTranspilerConfiguration();
        c.setComponentClassNodeResolver(componentClassNodeResolver);

        final var ct = new DefaultComponentTranspiler();
        final PositionSetter ps = new SimplePositionSetter();
        final LeftShiftFactory lsf = new DefaultLeftShiftFactory();
        final var gbnt = new DefaultGroovyBodyNodeTranspiler(ps, lsf);
        final var btt = new DefaultBodyTextTranspiler(gbnt, ps, lsf, true);
        final var bt = new DefaultBodyTranspiler(ct, btt);
        final var vnt = new DefaultValueNodeTranspiler(ct, ps);

        ct.setLeftShiftFactory(lsf);
        ct.setBodyTranspiler(bt);
        ct.setValueNodeTranspiler(vnt);
        ct.setComponentClassNodeResolver(componentClassNodeResolver);

        c.setComponentTranspiler(ct);
        c.setPositionSetter(ps);
        c.setLeftShiftFactory(lsf);
        c.setGroovyBodyNodeTranspiler(gbnt);
        c.setBodyTextTranspiler(btt);
        c.setBodyTranspiler(bt);
        c.setValueNodeTranspiler(vnt);

        return c;
    }

    private ComponentClassNodeResolver componentClassNodeResolver;
    private ComponentTranspiler componentTranspiler;
    private PositionSetter positionSetter;
    private LeftShiftFactory leftShiftFactory;
    private GroovyBodyNodeTranspiler groovyBodyNodeTranspiler;
    private BodyTextTranspiler bodyTextTranspiler;
    private BodyTranspiler bodyTranspiler;
    private ValueNodeTranspiler valueNodeTranspiler;

    public ComponentClassNodeResolver getComponentClassNodeResolver() {
        return Objects.requireNonNull(this.componentClassNodeResolver);
    }

    public void setComponentClassNodeResolver(ComponentClassNodeResolver componentClassNodeResolver) {
        this.componentClassNodeResolver = componentClassNodeResolver;
    }

    public ComponentTranspiler getComponentTranspiler() {
        return Objects.requireNonNull(this.componentTranspiler);
    }

    public void setComponentTranspiler(ComponentTranspiler componentTranspiler) {
        this.componentTranspiler = componentTranspiler;
    }

    @Override
    public PositionSetter getPositionSetter() {
        return Objects.requireNonNull(this.positionSetter);
    }

    public void setPositionSetter(PositionSetter positionSetter) {
        this.positionSetter = positionSetter;
    }

    public LeftShiftFactory getLeftShiftFactory() {
        return this.leftShiftFactory;
    }

    public void setLeftShiftFactory(LeftShiftFactory leftShiftFactory) {
        this.leftShiftFactory = leftShiftFactory;
    }

    public GroovyBodyNodeTranspiler getGroovyBodyNodeTranspiler() {
        return this.groovyBodyNodeTranspiler;
    }

    public void setGroovyBodyNodeTranspiler(GroovyBodyNodeTranspiler groovyBodyNodeTranspiler) {
        this.groovyBodyNodeTranspiler = groovyBodyNodeTranspiler;
    }

    public BodyTextTranspiler getBodyTextTranspiler() {
        return this.bodyTextTranspiler;
    }

    public void setBodyTextTranspiler(BodyTextTranspiler bodyTextTranspiler) {
        this.bodyTextTranspiler = bodyTextTranspiler;
    }

    @Override
    public BodyTranspiler getBodyTranspiler() {
        return Objects.requireNonNull(this.bodyTranspiler);
    }

    public void setBodyTranspiler(BodyTranspiler bodyTranspiler) {
        this.bodyTranspiler = bodyTranspiler;
    }

    public ValueNodeTranspiler getValueNodeTranspiler() {
        return Objects.requireNonNull(this.valueNodeTranspiler);
    }

    public void setValueNodeTranspiler(ValueNodeTranspiler valueNodeTranspiler) {
        this.valueNodeTranspiler = valueNodeTranspiler;
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
