package groowt.view.component.compiler;

import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.source.ComponentTemplateSource;

public interface ComponentTemplateCompileUnit {

    Class<? extends ViewComponent> getForClass();
    String getDefaultPackageName();
    ComponentTemplateSource getSource();
    ComponentTemplateCompileResult compile(ComponentTemplateCompilerConfiguration configuration)
            throws ComponentTemplateCompileException;

    default ComponentTemplateCompileResult compile() throws ComponentTemplateCompileException {
        return this.compile(new DefaultComponentTemplateCompilerConfiguration());
    }

}
