package groowt.view.component.web.compiler;

import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.AbstractComponentTemplateCompileUnit;
import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompileResult;
import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration;
import groowt.view.component.compiler.source.ComponentTemplateSource;
import groowt.view.component.compiler.source.FileSource;
import groowt.view.component.compiler.source.URISource;
import groowt.view.component.compiler.source.URLSource;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.io.ReaderSource;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.net.URI;

public class DefaultWebViewComponentTemplateCompileUnit extends AbstractComponentTemplateCompileUnit
        implements ReaderSource, WebViewComponentTemplateCompileUnit {

    private final String defaultPackageName;
    private final CompilationUnit groovyCompilationUnit = new CompilationUnit();

    public DefaultWebViewComponentTemplateCompileUnit(
            Class<? extends ViewComponent> forClass,
            ComponentTemplateSource source,
            String defaultPackageName
    ) {
        super(forClass, source);
        if (!defaultPackageName.isEmpty() && !defaultPackageName.endsWith(".")) {
            this.defaultPackageName = defaultPackageName + ".";
        } else {
            this.defaultPackageName = defaultPackageName;
        }
    }

    @Override
    public String getDefaultPackageName() {
        return this.defaultPackageName;
    }

    @Override
    public CompilationUnit getGroovyCompilationUnit() {
        return this.groovyCompilationUnit;
    }

    @Override
    public ReaderSource getGroovyReaderSource() {
        return this;
    }

    @Override
    public ComponentTemplateCompileResult compile(ComponentTemplateCompilerConfiguration configuration)
            throws ComponentTemplateCompileException {
        final WebViewComponentTemplateCompiler compiler = WebViewComponentTemplateCompiler.get();
        return compiler.compile(this);
    }

    @Override
    public Reader getReader() {
        try {
            return this.getSource().toReader();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canReopenSource() {
        return this.getSource().canReopen();
    }

    @Override
    public @Nullable String getLine(int lineNumber, Janitor janitor) {
        if (lineNumber > -1 && this.getSource().canReopen()) {
            return this.getSource().getLines().get(lineNumber);
        } else {
            return null;
        }
    }

    @Override
    public void cleanup() {}

    @Override
    public @Nullable URI getURI() {
        return switch (this.getSource()) {
            case FileSource fileSource -> fileSource.getURI();
            case URISource uriSource -> uriSource.getURI();
            case URLSource urlSource -> urlSource.getURI();
            default -> null;
        };
    }

}
