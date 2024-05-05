package groowt.view.component;

public interface ComponentTemplateCompiler {
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, TemplateSource source);
}
