package groowt.view.component.compiler;

import groowt.view.component.ViewComponent;

/**
 * Represents an exception thrown while attempting to instantiate a ComponentTemplate during compilation.
 */
public class ComponentTemplateCompileErrorException extends Exception {

    private final Class<? extends ViewComponent> forClass;
    private final Object templateSource;

    public ComponentTemplateCompileErrorException(
            String message,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(message);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public ComponentTemplateCompileErrorException(
            String message,
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(message, cause);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public ComponentTemplateCompileErrorException(
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(cause);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public ComponentTemplateCompileErrorException(
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super("Compile error in " + templateSource + " for " + forClass.getName());
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
