package groowt.view.component;

import groowt.view.View;

public interface ViewComponent extends View {

    default String getTypeName() {
        return this.getClass().getName();
    }

    void setContext(ComponentContext context);
    ComponentContext getContext();

}
