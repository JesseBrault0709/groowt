package groowt.view.component.runtime;

import groovy.lang.GString;
import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;
import org.jetbrains.annotations.ApiStatus;

public interface ComponentWriter {

    @ApiStatus.Internal
    void setRenderContext(RenderContext renderContext);

    @ApiStatus.Internal
    void setComponentContext(ComponentContext componentContext);

    void append(String string);
    void append(GString gString);
    void append(ViewComponent viewComponent);
    void append(Object object);
    
    @SuppressWarnings("unused")
    default void leftShift(Object object) {
        switch (object) {
            case String s -> this.append(s);
            case GString gs -> this.append(gs);
            case ViewComponent viewComponent -> this.append(viewComponent);
            default -> this.append(object);
        }
    }

}
