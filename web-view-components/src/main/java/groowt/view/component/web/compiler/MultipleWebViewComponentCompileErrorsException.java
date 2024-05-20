package groowt.view.component.web.compiler;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompileUnit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MultipleWebViewComponentCompileErrorsException extends ComponentTemplateCompileException {

    private final List<Throwable> errors = new ArrayList<>();

    public MultipleWebViewComponentCompileErrorsException(
            ComponentTemplateCompileUnit compileUnit,
            List<? extends Throwable> errors
    ) {
        super(compileUnit, "There were multiple errors during compilation/transpilation.");
        this.errors.addAll(errors);
    }

    public List<Throwable> getErrors() {
        return this.errors;
    }

    @Override
    public String getMessage() {
        final var sw = new StringWriter();
        sw.append(super.getMessage()).append("\n\n");
        for (int i = 0; i < this.errors.size(); i++) {
            final var error = this.errors.get(i);
            sw.append(String.format("Error no. %d:\n", i + 1));
            error.printStackTrace(new PrintWriter(sw));
            sw.append("\n");
        }
        return sw.toString();
    }

}
