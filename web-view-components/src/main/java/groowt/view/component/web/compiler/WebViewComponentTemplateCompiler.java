package groowt.view.component.web.compiler;

import groowt.view.component.compiler.*;
import groowt.view.component.compiler.source.ComponentTemplateSource;

import java.util.ServiceLoader;

public interface WebViewComponentTemplateCompiler
        extends ComponentTemplateCompiler<WebViewComponentTemplateCompileUnit> {

    static WebViewComponentTemplateCompiler get() {
        return get(new DefaultComponentTemplateCompilerConfiguration());
    }

    static WebViewComponentTemplateCompiler get(ComponentTemplateCompilerConfiguration configuration) {
        final ServiceLoader<WebViewComponentTemplateCompilerFactory> factoryServiceLoader =
                ServiceLoader.load(WebViewComponentTemplateCompilerFactory.class);
        final var factory = factoryServiceLoader.findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Could not load a WebViewComponentTemplateCompiler " +
                                "using the thread's context classLoader."
                ));
        return factory.create(configuration);
    }

    default ComponentTemplateCompileResult compileAnonymous(ComponentTemplateSource source, String packageName)
            throws ComponentTemplateCompileException {
        return this.compile(new DefaultWebViewComponentTemplateCompileUnit(
                AnonymousWebViewComponent.class,
                source,
                packageName
        ));
    }

}
