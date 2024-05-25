package groowt.view.component.web.transpile;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileUnit;

public interface GroovyTranspiler {

    WebViewComponentSourceUnit transpile(
            ComponentTemplateCompilerConfiguration compilerConfiguration,
            WebViewComponentTemplateCompileUnit compileUnit,
            CompilationUnitNode compilationUnitNode,
            String templateClassSimpleName
    ) throws ComponentTemplateCompileException;

}
