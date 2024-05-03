package groowt.view.component;

public class MissingFragmentTypeException extends MissingComponentException {

    public MissingFragmentTypeException(ComponentTemplate template, int line, int col, Throwable cause) {
        super(template, cause, line, col);
    }

    @Override
    protected String getMissingKeyName() {
        return "fragment type";
    }

}
