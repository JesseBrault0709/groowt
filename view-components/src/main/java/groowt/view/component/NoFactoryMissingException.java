package groowt.view.component;

public class NoFactoryMissingException extends UnsupportedOperationException {

    public NoFactoryMissingException() {}

    public NoFactoryMissingException(String message) {
        super(message);
    }

    public NoFactoryMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoFactoryMissingException(Throwable cause) {
        super(cause);
    }

}
