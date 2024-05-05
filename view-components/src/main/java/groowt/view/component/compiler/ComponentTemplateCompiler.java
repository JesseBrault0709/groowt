package groowt.view.component.compiler;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentTemplateSource;

public interface ComponentTemplateCompiler {
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, ComponentTemplateSource source);
}
