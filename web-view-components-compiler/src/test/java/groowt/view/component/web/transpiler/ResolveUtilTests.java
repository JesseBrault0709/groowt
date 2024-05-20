package groowt.view.component.web.transpiler;

import org.junit.jupiter.api.Test;

import static groowt.view.component.web.transpile.resolve.ResolveUtil.convertCanonicalNameToBinaryName;
import static org.junit.jupiter.api.Assertions.*;

public class ResolveUtilTests {

    @Test
    public void abcABC() {
        assertEquals("a.b.c.A$B$C", convertCanonicalNameToBinaryName("a.b.c.A.B.C"));
    }

    @Test
    public void ABC() {
        assertEquals("A$B$C", convertCanonicalNameToBinaryName("A.B.C"));
    }

    @Test
    public void abcA() {
        assertEquals("a.b.c.A", convertCanonicalNameToBinaryName("a.b.c.A"));
    }

}
