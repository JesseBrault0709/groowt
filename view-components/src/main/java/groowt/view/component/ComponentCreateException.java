package groowt.view.component;

public class ComponentCreateException extends RuntimeException {

    private final ComponentTemplate template;
    private final int line;
    private final int column;

    public ComponentCreateException(ComponentTemplate template, int line, int column, Throwable cause) {
        super(cause);
        this.template = template;
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return "Exception while rendering " + this.template.getClass()
                + " at line " + this.line + ", column " + this.column + ".";
    }

}
