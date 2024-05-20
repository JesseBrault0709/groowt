package groowt.view.component.web.compiler;

import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration;

public interface WebViewComponentTemplateCompilerFactory {
    WebViewComponentTemplateCompiler create(ComponentTemplateCompilerConfiguration configuration);
}
