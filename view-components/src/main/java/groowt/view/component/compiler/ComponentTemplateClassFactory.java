package groowt.view.component.compiler;

import groowt.view.component.ComponentTemplate;

public interface ComponentTemplateClassFactory {
    Class<? extends ComponentTemplate> getTemplateClass(ComponentTemplateCompileResult compileResult);
}
