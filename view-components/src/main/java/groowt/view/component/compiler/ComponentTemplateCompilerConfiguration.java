package groowt.view.component.compiler;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;

public interface ComponentTemplateCompilerConfiguration {
    GroovyClassLoader getGroovyClassLoader();
    CompilerConfiguration getGroovyCompilerConfiguration();
    CompilePhase getToCompilePhase();
}
