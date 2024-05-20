package groowt.view.web.transpiler;

import groovy.lang.Tuple2;
import groowt.view.web.transpile.DefaultGroovyTranspiler;
import groowt.view.web.transpile.GroovyTranspiler;
import org.codehaus.groovy.control.CompilationUnit;

public class DefaultGroovyTranspilerTests extends GroovyTranspilerTests {

    protected static Tuple2<GroovyTranspiler, CompilationUnit> getDefaultGroovyTranspiler() {
        return new Tuple2<>(new DefaultGroovyTranspiler(), new CompilationUnit());
    }

    public DefaultGroovyTranspilerTests() {
        super(getDefaultGroovyTranspiler());
    }

}
