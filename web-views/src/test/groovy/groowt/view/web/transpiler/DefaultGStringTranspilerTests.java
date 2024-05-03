package groowt.view.web.transpiler;

import groowt.view.web.transpile.DefaultGStringTranspiler;
import groowt.view.web.transpile.DefaultJStringTranspiler;
import groowt.view.web.transpile.GStringTranspiler;
import groowt.view.web.transpile.SimplePositionSetter;

public class DefaultGStringTranspilerTests extends GStringTranspilerTests {

    @Override
    protected GStringTranspiler getGStringTranspiler() {
        final var positionSetter = new SimplePositionSetter();
        return new DefaultGStringTranspiler(positionSetter, new DefaultJStringTranspiler(positionSetter));
    }

}
