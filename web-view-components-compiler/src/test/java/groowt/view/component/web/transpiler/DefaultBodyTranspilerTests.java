package groowt.view.component.web.transpiler;

import groowt.view.component.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.component.web.transpile.SimpleTranspilerConfiguration;
import groowt.view.component.web.transpile.TranspilerConfiguration;
import groowt.view.component.web.transpile.resolve.CachingComponentClassNodeResolver;
import org.codehaus.groovy.ast.ModuleNode;

public class DefaultBodyTranspilerTests extends BodyTranspilerTests {

    @Override
    protected TranspilerConfiguration getConfiguration(
            WebViewComponentTemplateCompileUnit compileUnit,
            ModuleNode moduleNode
    ) {
        return SimpleTranspilerConfiguration.withDefaults(new CachingComponentClassNodeResolver(compileUnit));
    }

}
