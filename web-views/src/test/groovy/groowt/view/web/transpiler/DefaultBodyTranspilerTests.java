package groowt.view.web.transpiler;

import groowt.view.web.transpile.*;

public class DefaultBodyTranspilerTests extends BodyTranspilerTests {

    @Override
    protected BodyTranspiler getBodyTranspiler() {
        final var positionSetter = new SimplePositionSetter();
        final var jStringTranspiler = new DefaultJStringTranspiler(positionSetter);
        final var gStringTranspiler = new DefaultGStringTranspiler(positionSetter, jStringTranspiler);
        final var componentTranspiler = new DefaultComponentTranspiler();
        final var valueNodeTranspiler = new DefaultValueNodeTranspiler(componentTranspiler);
        componentTranspiler.setValueNodeTranspiler(valueNodeTranspiler);
        final var bodyTranspiler = new DefaultBodyTranspiler(gStringTranspiler, jStringTranspiler, componentTranspiler);
        componentTranspiler.setBodyTranspiler(bodyTranspiler);
        return bodyTranspiler;
    }

    @Override
    protected OutStatementFactory getOutStatementFactory() {
        return new SimpleOutStatementFactory();
    }

}
