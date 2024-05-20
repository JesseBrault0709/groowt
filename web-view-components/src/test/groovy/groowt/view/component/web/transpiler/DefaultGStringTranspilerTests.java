package groowt.view.component.web.transpiler;

import groowt.view.component.web.transpile.DefaultGStringTranspiler;
import groowt.view.component.web.transpile.DefaultJStringTranspiler;
import groowt.view.component.web.transpile.GStringTranspiler;
import groowt.view.component.web.transpile.SimplePositionSetter;

public class DefaultGStringTranspilerTests extends GStringTranspilerTests {

    @Override
    protected GStringTranspiler getGStringTranspiler() {
        final var positionSetter = new SimplePositionSetter();
        return new DefaultGStringTranspiler(positionSetter, new DefaultJStringTranspiler(positionSetter));
    }

}
