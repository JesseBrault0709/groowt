package groowt.view.component;

/**
 * An exception which signals that a component of the given type
 * could not be created in the given template.
 */
public class ComponentCreateException extends RuntimeException {

    private final Object componentType;
    private final ComponentTemplate template;
    private final int line;
    private final int column;

    public ComponentCreateException(
            Object componentType,
            ComponentTemplate template,
            int line,
            int column,
            Throwable cause
    ) {
        super(cause);
        this.componentType = componentType;
        this.template = template;
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return "Exception in " +  this.template.getClass() + " while creating " + this.componentType.getClass() +
                " at line " + this.line + ", column " + this.column + ".";
    }

}
