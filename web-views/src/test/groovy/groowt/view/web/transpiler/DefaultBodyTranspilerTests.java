package groowt.view.web.transpiler;

import groowt.view.web.transpile.*;

public class DefaultBodyTranspilerTests extends BodyTranspilerTests {

    @Override
    protected TranspilerConfiguration getConfiguration() {
        return new DefaultTranspilerConfiguration();
    }

}
