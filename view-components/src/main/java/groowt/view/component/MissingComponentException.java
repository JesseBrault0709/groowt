package groowt.view.component;

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
        return "Missing " + this.getMissingKeyName() + " on line " + this.line +  ", column " + this.col + ".";
    }

}
