package groowt.view.component.web.compiler;

import groowt.view.component.compiler.ComponentTemplateCompileUnit;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.io.ReaderSource;

public interface WebViewComponentTemplateCompileUnit extends ComponentTemplateCompileUnit {
    CompilationUnit getGroovyCompilationUnit();
    ReaderSource getGroovyReaderSource();
}
