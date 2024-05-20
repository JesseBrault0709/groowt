package groowt.view.component.web.compiler;

import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration;

public class DefaultWebViewComponentTemplateCompilerFactory implements WebViewComponentTemplateCompilerFactory {

    @Override
    public WebViewComponentTemplateCompiler create(ComponentTemplateCompilerConfiguration configuration) {
        return new DefaultWebViewComponentTemplateCompiler(configuration);
    }

}
