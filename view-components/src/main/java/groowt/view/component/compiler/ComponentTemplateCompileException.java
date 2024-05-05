package groowt.view.component.compiler;

import groowt.view.component.ViewComponent;

/**
 * Represents an exception thrown while attempting to instantiate a ComponentTemplate during compilation.
 */
public class ComponentTemplateCompileException extends RuntimeException {

    private final Class<? extends ViewComponent> forClass;
    private final Object templateSource;

    public ComponentTemplateCompileException(
            String message,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(message);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public ComponentTemplateCompileException(
            String message,
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(message, cause);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public ComponentTemplateCompileException(
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(cause);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public Class<? extends ViewComponent> getForClass() {
        return this.forClass;
    }

    public Object getTemplateSource() {
        return this.templateSource;
    }

}
