package groowt.view.web.transpiler;

import groowt.view.web.transpile.PreambleTranspiler;
import org.junit.jupiter.api.Test;

public abstract class PreambleTranspilerTests {

    protected final PreambleTranspiler preambleTranspiler;

    public PreambleTranspilerTests(PreambleTranspiler preambleTranspiler) {
        this.preambleTranspiler = preambleTranspiler;
    }

    @Test
    public void smokeScreen() {}

}
