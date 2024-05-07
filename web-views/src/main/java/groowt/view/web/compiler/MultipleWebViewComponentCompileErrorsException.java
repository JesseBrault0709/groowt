package groowt.view.web.compiler;

import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.ComponentTemplateCompileErrorException;

import java.util.ArrayList;
import java.util.List;

public class MultipleWebViewComponentCompileErrorsException extends ComponentTemplateCompileErrorException {

    private final List<Throwable> errors = new ArrayList<>();

    public MultipleWebViewComponentCompileErrorsException(
            String message,
            List<? extends Throwable> errors,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(message, forClass, templateSource);
        this.errors.addAll(errors);
    }

    public MultipleWebViewComponentCompileErrorsException(
            List<? extends Throwable> errors,
            Class<? extends ViewComponent> forClass,
            Object templateSource
    ) {
        super(forClass, templateSource);
        this.errors.addAll(errors);
    }

    public List<Throwable> getErrors() {
        return this.errors;
    }

}
