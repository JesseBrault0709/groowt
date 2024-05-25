package groowt.view.component.compiler;

import org.jetbrains.annotations.Nullable;

public class ComponentTemplateCompileException extends Exception {

    private final ComponentTemplateCompileUnit compileUnit;

    public ComponentTemplateCompileException(ComponentTemplateCompileUnit compileUnit, String message) {
        super(message);
        this.compileUnit = compileUnit;
    }

    public ComponentTemplateCompileException(
            ComponentTemplateCompileUnit compileUnit,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.compileUnit = compileUnit;
    }

    @Override
    public String getMessage() {
        final var sb = new StringBuilder("Error in ").append(compileUnit.getSource().getDescriptiveName());
        final @Nullable String position = this.formatPosition();
        if (position != null) {
            sb.append(" at ").append(position);
        }
        return sb.append(": ").append(super.getMessage()).toString();
    }

    protected @Nullable String formatPosition() {
        return null;
    }

}
