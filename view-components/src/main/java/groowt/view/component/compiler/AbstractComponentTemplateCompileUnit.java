package groowt.view.component.compiler;

import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.source.ComponentTemplateSource;

public abstract class AbstractComponentTemplateCompileUnit implements
        ComponentTemplateCompileUnit {

    private final Class<? extends ViewComponent> forClass;
    private final ComponentTemplateSource source;

    public AbstractComponentTemplateCompileUnit(
            Class<? extends ViewComponent> forClass,
            ComponentTemplateSource source
    ) {
        this.forClass = forClass;
        this.source = source;
    }

    @Override
    public Class<? extends ViewComponent> getForClass() {
        return this.forClass;
    }

    @Override
    public ComponentTemplateSource getSource() {
        return this.source;
    }

}
