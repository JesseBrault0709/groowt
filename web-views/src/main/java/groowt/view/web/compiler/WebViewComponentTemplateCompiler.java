package groowt.view.web.compiler;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.compiler.ComponentTemplateCompileErrorException;
import groowt.view.component.compiler.ComponentTemplateCompiler;
import groowt.view.component.factory.ComponentTemplateSource;

public interface WebViewComponentTemplateCompiler extends ComponentTemplateCompiler {
    ComponentTemplateCompileResult compileAnonymous(ComponentTemplateSource source) throws ComponentTemplateCompileErrorException;
    ComponentTemplate compileAndGetAnonymous(ComponentTemplateSource source) throws ComponentTemplateCompileErrorException;
}
