package groowt.view.component;

import groowt.view.View;
import org.jetbrains.annotations.ApiStatus;

public interface ViewComponent extends View {

    default String getTypeName() {
        return this.getClass().getName();
    }

    @ApiStatus.Internal
    void setContext(ComponentContext context);

    ComponentContext getContext();

}
