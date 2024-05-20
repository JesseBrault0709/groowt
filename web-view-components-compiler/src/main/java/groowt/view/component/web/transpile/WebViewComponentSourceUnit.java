package groowt.view.component.web.transpile;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.io.ReaderSource;
import org.codehaus.groovy.syntax.Reduction;

import java.util.Objects;

public class WebViewComponentSourceUnit extends SourceUnit {

    private WebViewComponentModuleNode moduleNode;

    public WebViewComponentSourceUnit(
            String name,
            ReaderSource source,
            CompilerConfiguration configuration,
            GroovyClassLoader loader,
            ErrorCollector er
    ) {
        super(name, source, configuration, loader, er);
    }

    public void setModuleNode(WebViewComponentModuleNode moduleNode) {
        this.moduleNode = moduleNode;
    }

    @Override
    public Reduction getCST() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModuleNode getAST() {
        return Objects.requireNonNull(this.moduleNode);
    }

    @Override
    public void parse() throws CompilationFailedException {
        // No-op
    }

    @Override
    public void convert() throws CompilationFailedException {
        // No-op
    }

    @Override
    public ModuleNode buildAST() {
        return this.getAST();
    }

}
