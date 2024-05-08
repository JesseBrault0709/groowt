package groowt.view.component;

public class ViewComponentBugError extends RuntimeException {

    public ViewComponentBugError(String message) {
        super(message);
    }

    public ViewComponentBugError(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewComponentBugError(Throwable cause) {
        super(cause);
    }

    public ViewComponentBugError(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessage() {
        return "BUG! Please file an issue report at the github repository. " + super.getMessage();
    }

}
