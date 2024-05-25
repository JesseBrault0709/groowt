package groowt.view.component.compiler;

import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.source.ComponentTemplateSource;

public abstract class AbstractComponentTemplateCompileUnit implements
        ComponentTemplateCompileUnit {

    private final String descriptiveName;
    private final Class<? extends ViewComponent> forClass;
    private final ComponentTemplateSource source;

    public AbstractComponentTemplateCompileUnit(
            String descriptiveName,
            Class<? extends ViewComponent> forClass,
            ComponentTemplateSource source
    ) {
        this.descriptiveName = descriptiveName;
        this.forClass = forClass;
        this.source = source;
    }

    @Override
    public String getDescriptiveName() {
        return this.descriptiveName;
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
