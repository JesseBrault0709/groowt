package groowt.view.component;

import java.io.Reader;

public class ComponentTemplateCreateException extends RuntimeException {

    private final Class<? extends ViewComponent> forClass;
    private final Object templateSource;

    public ComponentTemplateCreateException(
            String message,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(message);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public ComponentTemplateCreateException(
            String message,
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(message, cause);
        this.forClass = forClass;
        this.templateSource = templateSource;
    }

    public ComponentTemplateCreateException(
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
