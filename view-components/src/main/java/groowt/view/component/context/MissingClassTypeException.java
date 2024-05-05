package groowt.view.component.context;

import groowt.view.component.ComponentTemplate;

public class MissingClassTypeException extends MissingComponentException {

    private final String typeName;

    public MissingClassTypeException(ComponentTemplate template, String typeName, int line, int col, Throwable cause) {
        super(template, cause, line, col);
        this.typeName = typeName;
    }

    @Override
    protected String getMissingKeyName() {
        return "class component " + this.typeName;
    }

}
