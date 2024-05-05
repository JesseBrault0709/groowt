package groowt.view.component.context;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.context.ComponentContext;

/**
 * An exception which represents that a component type could not be
 * found by the {@link ComponentContext}.
 */
public abstract class MissingComponentException extends RuntimeException {

    private final ComponentTemplate template;
    private final int line;
    private final int col;

    public MissingComponentException(ComponentTemplate template, Throwable cause, int line, int col) {
        super(cause);
        this.template = template;
        this.line = line;
        this.col = col;
    }

    protected abstract String getMissingKeyName();

    @Override
    public String getMessage() {
        return "In " + this.template + " missing " + this.getMissingKeyName()
                + " on line " + this.line +  ", column " + this.col + ".";
    }

}