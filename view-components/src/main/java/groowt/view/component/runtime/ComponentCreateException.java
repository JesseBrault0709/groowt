package groowt.view.component.runtime;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.runtime.RenderContext.ResolvedStringType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * An exception which signals that a component of the given type
 * could not be created in the given template.
 */
public class ComponentCreateException extends RuntimeException {

    private final RenderContext.Resolved<?> resolved;

    private ComponentTemplate template;
    private int line;
    private int column;

    public ComponentCreateException(
            RenderContext.Resolved<?> resolved,
            Throwable cause
    ) {
        super(cause);
        this.resolved = resolved;
    }

    public RenderContext.Resolved<?> getResolved() {
        return this.resolved;
    }

    public ComponentTemplate getTemplate() {
        return this.template;
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
        final var sb = new StringBuilder("Exception in ")
                .append(this.template.getClass().getName());
        if (this.resolved instanceof ResolvedStringType<?> resolvedStringType) {
            sb.append(" while creating string-typed component ")
                    .append(resolvedStringType.typeName())
                    .append(" (using ")
                    .append(resolvedStringType.resolvedType().getName())
                    .append(") ");
        } else if (this.resolved instanceof RenderContext.ResolvedClassType<?> resolvedClassType) {
            sb.append(" while creating class-typed component ")
                    .append(resolvedClassType.alias())
                    .append(" of public type ")
                    .append(resolvedClassType.requestedType())
                    .append(" (using ")
                    .append(resolvedClassType.resolvedType())
                    .append(") ");
        } else {
            sb.append(" while creating unknown-typed component (using ")
                    .append(resolved.resolvedType().getName())
                    .append(") ");
        }
        return sb.append(" at line ")
                .append(this.line)
                .append(", column ")
                .append(this.column)
                .append(".")
                .toString();
    }

}
