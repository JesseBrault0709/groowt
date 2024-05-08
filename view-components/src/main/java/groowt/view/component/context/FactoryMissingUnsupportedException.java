package groowt.view.component.context;

import groowt.view.component.ViewComponent;
import org.jetbrains.annotations.Nullable;

public class FactoryMissingUnsupportedException extends ComponentResolveException {

    private final String message;

    public FactoryMissingUnsupportedException(@Nullable String message, String typeName) {
        super(typeName);
        this.message = message;
    }

    public FactoryMissingUnsupportedException(
            @Nullable String message,
            String typeName,
            @Nullable Class<? extends ViewComponent> type
    ) {
        super(typeName, type);
        this.message = message;
    }

    public FactoryMissingUnsupportedException(@Nullable String message, String typeName, Throwable cause) {
        super(typeName, cause);
        this.message = message;
    }

    public FactoryMissingUnsupportedException(
            @Nullable String message,
            String typeName,
            @Nullable Class<? extends ViewComponent> type,
            Throwable cause
    ) {
        super(typeName, type, cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (this.message != null) {
            return super.getMessage() + " " + this.message;
        } else {
            return super.getMessage();
        }
    }

}
