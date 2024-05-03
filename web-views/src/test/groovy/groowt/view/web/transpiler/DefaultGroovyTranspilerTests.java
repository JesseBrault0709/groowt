package groowt.view.web.transpiler;

import groovy.lang.Tuple2;
import groowt.util.di.DefaultRegistryObjectFactory;
import groowt.view.web.transpile.DefaultGroovyTranspiler;
import groowt.view.web.transpile.DefaultTranspilerConfiguration;
import groowt.view.web.transpile.GroovyTranspiler;
import org.codehaus.groovy.control.CompilationUnit;

public class DefaultGroovyTranspilerTests extends GroovyTranspilerTests {

    protected static Tuple2<GroovyTranspiler, CompilationUnit> getDefaultGroovyTranspiler() {
        final var cu = new CompilationUnit();
        return new Tuple2<>(
                new DefaultGroovyTranspiler(cu, "groowt.view.web.transpiler", DefaultTranspilerConfiguration::new),
                cu
        );
    }

    public DefaultGroovyTranspilerTests() {
        super(getDefaultGroovyTranspiler());
    }

}
