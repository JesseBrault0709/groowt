package groowt.view.component.compiler;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;

import static java.util.Objects.requireNonNull;

public class DefaultComponentTemplateCompilerConfiguration implements ComponentTemplateCompilerConfiguration {

    private GroovyClassLoader groovyClassLoader;
    private CompilerConfiguration groovyCompilerConfiguration;
    private CompilePhase toCompilePhase;

    public DefaultComponentTemplateCompilerConfiguration() {
        this.groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        this.groovyCompilerConfiguration = new CompilerConfiguration();
        this.toCompilePhase = CompilePhase.CLASS_GENERATION;
    }

    @Override
    public GroovyClassLoader getGroovyClassLoader() {
        return this.groovyClassLoader;
    }

    public void setGroovyClassLoader(GroovyClassLoader groovyClassLoader) {
        this.groovyClassLoader = requireNonNull(groovyClassLoader);
    }

    @Override
    public CompilerConfiguration getGroovyCompilerConfiguration() {
        return this.groovyCompilerConfiguration;
    }

    public void setGroovyCompilerConfiguration(CompilerConfiguration groovyCompilerConfiguration) {
        this.groovyCompilerConfiguration = requireNonNull(groovyCompilerConfiguration);
    }

    @Override
    public CompilePhase getToCompilePhase() {
        return this.toCompilePhase;
    }

    public void setToCompilePhase(CompilePhase toCompilePhase) {
        this.toCompilePhase = requireNonNull(toCompilePhase);
    }

}
