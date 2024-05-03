package groowt.view.web.transpiler;

import groowt.view.web.transpile.DefaultPreambleTranspiler;

public class DefaultPreambleTranspilerTests extends PreambleTranspilerTests {

    protected static DefaultPreambleTranspiler getDefaultPreambleTranspiler() {
        return new DefaultPreambleTranspiler();
    }

    public DefaultPreambleTranspilerTests() {
        super(getDefaultPreambleTranspiler());
    }

}
