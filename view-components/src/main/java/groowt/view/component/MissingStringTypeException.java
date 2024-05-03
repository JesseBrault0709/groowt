package groowt.view.component;

public abstract class MissingStringTypeException extends MissingComponentException {

    private final String keyName;

    public MissingStringTypeException(ComponentTemplate template, String keyName, int line, int col, Throwable cause) {
        super(template, cause, line, col);
        this.keyName = keyName;
    }

    @Override
    protected String getMissingKeyName() {
        return "string-typed component " + this.keyName;
    }

}
