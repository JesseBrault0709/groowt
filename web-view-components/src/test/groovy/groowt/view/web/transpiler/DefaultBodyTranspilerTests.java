package groowt.view.web.transpiler;

import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.web.transpile.DefaultTranspilerConfiguration;
import groowt.view.web.transpile.TranspilerConfiguration;
import groowt.view.web.transpile.resolve.CachingComponentClassNodeResolver;
import org.codehaus.groovy.ast.ModuleNode;

public class DefaultBodyTranspilerTests extends BodyTranspilerTests {

    @Override
    protected TranspilerConfiguration getConfiguration(
            WebViewComponentTemplateCompileUnit compileUnit,
            ModuleNode moduleNode
    ) {
        return new DefaultTranspilerConfiguration(
                new CachingComponentClassNodeResolver(compileUnit)
        );
    }

}
