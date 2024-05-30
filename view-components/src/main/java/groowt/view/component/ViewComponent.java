package groowt.view.component;

import groowt.view.View;
import groowt.view.component.context.ComponentContext;

public interface ViewComponent extends View {

    ComponentTemplate getComponentTemplate();
    void setComponentTemplate(ComponentTemplate componentTemplate);

    /**
     * <em>Note:</em> compiled templates are required to automatically
     * call this method after the component is constructed. One
     * only needs to use this if doing custom rendering logic
     * where the component is not rendered inside a compiled
     * template.
     */
    void setContext(ComponentContext context);

    /**
     * <em>Note:</em> compiled templates call the
     * related {@link #setContext} method <strong>after</strong>
     * the component is instantiated. If one needs access to the
     * {@link ComponentContext} in the component constructor,
     * ask for it in the constructor parameters.
     *
     * @return the component context
     */
    ComponentContext getContext();

}
