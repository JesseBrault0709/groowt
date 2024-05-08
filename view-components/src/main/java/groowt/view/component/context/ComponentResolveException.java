package groowt.view.component.context;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ComponentResolveException extends Exception {

    private final String typeNameOrAlias;
    private @Nullable String message;
    private @Nullable Class<? extends ViewComponent> type;
    private @Nullable ComponentTemplate template;
    private int line;
    private int column;

    public ComponentResolveException(String typeName) {
        this.typeNameOrAlias = Objects.requireNonNull(typeName);
    }

    public ComponentResolveException(String typeName, @Nullable Class<? extends ViewComponent> type) {
        this.typeNameOrAlias = Objects.requireNonNull(typeName);
        this.type = type;
    }

    public ComponentResolveException(
            @Nullable String message,
            String typeName,
            @Nullable Class<? extends ViewComponent> type
    ) {
        this.typeNameOrAlias = typeName;
        this.message = message;
        this.type = type;
    }

    public ComponentResolveException(String typeName, Throwable cause) {
        super(cause);
        this.typeNameOrAlias = Objects.requireNonNull(typeName);
    }

    public ComponentResolveException(String typeName, @Nullable Class<? extends ViewComponent> type, Throwable cause) {
        super(cause);
        this.typeNameOrAlias = Objects.requireNonNull(typeName);
        this.type = type;
    }

    public ComponentResolveException(
            @NotNull ComponentTemplate template,
            Throwable cause,
            String typeName,
            int line,
            int column
    ) {
        super(cause);
        this.template = template;
        this.typeNameOrAlias = typeName;
        this.line = line;
        this.column = column;
    }

    public ComponentResolveException(
            @NotNull ComponentTemplate template,
            Throwable cause,
            String alias,
            @NotNull Class<? extends ViewComponent> type,
            int line,
            int column
    ) {
        super(cause);
        this.template = Objects.requireNonNull(template);
        this.typeNameOrAlias = alias;
        this.type = type;
        this.line = line;
        this.column = column;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    public String getTypeNameOrAlias() {
        return this.typeNameOrAlias;
    }

    public @Nullable Class<? extends ViewComponent> getType() {
        return this.type;
    }

    @ApiStatus.Internal
    public void setType(@Nullable Class<? extends ViewComponent> type) {
        this.type = type;
    }

    public @NotNull ComponentTemplate getTemplate() {
        return Objects.requireNonNull(this.template);
    }

    @ApiStatus.Internal
    public void setTemplate(ComponentTemplate template) {
        this.template = Objects.requireNonNull(template);
    }

    public int getLine() {
        return this.line;
    }

    @ApiStatus.Internal
    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return this.column;
    }

    @ApiStatus.Internal
    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String getMessage() {
        final var sb = new StringBuilder("Exception");
        if (this.template != null) {
            sb.append(" in ").append(this.template.getClass().getName());
        }
        sb.append(" while resolving component ").append(this.typeNameOrAlias);
        if (this.type != null) {
            sb.append(" of type ").append(this.type.getName());
        }
        sb.append(" at line ").append(this.line).append(", column ").append(this.column).append(".");
        if (this.message != null) {
            sb.append(" ").append(this.message);
        } // else, assume caused by is not null
        return sb.toString();
    }

}
