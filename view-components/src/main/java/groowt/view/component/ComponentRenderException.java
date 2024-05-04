package groowt.view.component;

/**
 * An exception which represents an error during rendering of a component.
 */
public class ComponentRenderException extends RuntimeException {

    private static String formatMessage(int line, int column) {
        return "Exception while rendering at line " + line + ", column " + column + ".";
    }

    private static String formatMessage(ViewComponent viewComponent) {
        return "Exception while rendering " + viewComponent;
    }

    private static String formatMessage(ViewComponent viewComponent, int line, int column) {
        return "Exception while rendering " + viewComponent + " at line " + line + ", column " + column + ".";
    }

    public ComponentRenderException() {}

    public ComponentRenderException(String message) {
        super(message);
    }

    public ComponentRenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentRenderException(Throwable cause) {
        super(cause);
    }

    public ComponentRenderException(int line, int column, Throwable cause) {
        super(formatMessage(line, column), cause);
    }

    public ComponentRenderException(ViewComponent component, Throwable cause) {
        super(formatMessage(component), cause);
    }

    public ComponentRenderException(ViewComponent component, int line, int column, Throwable cause) {
        super(formatMessage(component, line, column), cause);
    }

}
