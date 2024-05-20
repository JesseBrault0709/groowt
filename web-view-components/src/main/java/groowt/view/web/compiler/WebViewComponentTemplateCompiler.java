package groowt.view.web.compiler;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompileResult;
import groowt.view.component.compiler.ComponentTemplateCompiler;
import groowt.view.component.compiler.source.ComponentTemplateSource;

public interface WebViewComponentTemplateCompiler
        extends ComponentTemplateCompiler<WebViewComponentTemplateCompileUnit> {

    default ComponentTemplateCompileResult compileAnonymous(ComponentTemplateSource source, String packageName)
            throws ComponentTemplateCompileException {
        return this.compile(new WebViewComponentTemplateCompileUnit(
                AnonymousWebViewComponent.class,
                source,
                packageName
        ));
    }

}
