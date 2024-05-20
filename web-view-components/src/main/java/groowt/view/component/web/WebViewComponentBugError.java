package groowt.view.component.web;

public class WebViewComponentBugError extends RuntimeException {

    public WebViewComponentBugError(String message) {
        super(message);
    }

    public WebViewComponentBugError(String message, Throwable cause) {
        super(message, cause);
    }

    public WebViewComponentBugError(Throwable cause) {
        super(cause);
    }

    public WebViewComponentBugError(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessage() {
        return "BUG! Please file an issue at the github repository. " + super.getMessage();
    }

}
