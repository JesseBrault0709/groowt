package groowt.view.web;

import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.web.ast.node.Node;
import groowt.view.web.util.SourcePosition;

public class WebViewComponentTemplateCompileException extends ComponentTemplateCompileException {

    private final Node node;

    public WebViewComponentTemplateCompileException(
            String message,
            Class<? extends ViewComponent> forClass,
            Object templateSource,
            Node node
    ) {
        super(message, forClass, templateSource);
        this.node = node;
    }

    public WebViewComponentTemplateCompileException(
            String message,
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource,
            Node node
    ) {
        super(message, cause, forClass, templateSource);
        this.node = node;
    }

    public WebViewComponentTemplateCompileException(
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource,
            Node node
    ) {
        super(cause, forClass, templateSource);
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    @Override
    public String getMessage() {
        final SourcePosition start = this.node.getTokenRange().getStartPosition();
        return "Line " + start.line() + ", column " + start.column() + ": " + super.getMessage();
    }

}
