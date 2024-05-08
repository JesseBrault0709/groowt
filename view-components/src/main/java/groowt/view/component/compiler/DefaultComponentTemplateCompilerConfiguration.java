package groowt.view.component.compiler;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;

public class DefaultComponentTemplateCompilerConfiguration implements ComponentTemplateCompilerConfiguration {

    private GroovyClassLoader groovyClassLoader;
    private CompilerConfiguration groovyCompilerConfiguration;
    private CompilePhase toCompilePhase;

    @Override
    public GroovyClassLoader getGroovyClassLoader() {
        return this.groovyClassLoader != null
                ? this.groovyClassLoader
                : new GroovyClassLoader(this.getClass().getClassLoader());
    }

    public void setGroovyClassLoader(GroovyClassLoader groovyClassLoader) {
        this.groovyClassLoader = groovyClassLoader;
    }

    @Override
    public CompilerConfiguration getGroovyCompilerConfiguration() {
        return this.groovyCompilerConfiguration != null
                ? this.groovyCompilerConfiguration
                : CompilerConfiguration.DEFAULT;
    }

    public void setGroovyCompilerConfiguration(CompilerConfiguration groovyCompilerConfiguration) {
        this.groovyCompilerConfiguration = groovyCompilerConfiguration;
    }

    @Override
    public CompilePhase getToCompilePhase() {
        return this.toCompilePhase != null ? this.toCompilePhase : CompilePhase.CLASS_GENERATION;
    }

    public void setToCompilePhase(CompilePhase toCompilePhase) {
        this.toCompilePhase = toCompilePhase;
    }

}
