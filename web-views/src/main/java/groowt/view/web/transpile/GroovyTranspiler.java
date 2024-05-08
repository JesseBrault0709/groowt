package groowt.view.web.transpile;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit;

public interface GroovyTranspiler {

    WebViewComponentSourceUnit transpile(
            ComponentTemplateCompilerConfiguration compilerConfiguration,
            WebViewComponentTemplateCompileUnit compileUnit,
            CompilationUnitNode compilationUnitNode,
            String templateClassName
    ) throws ComponentTemplateCompileException;

}
