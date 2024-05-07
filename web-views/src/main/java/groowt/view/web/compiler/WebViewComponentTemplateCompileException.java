package groowt.view.web.compiler;

import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.ComponentTemplateCompileErrorException;
import groowt.view.web.ast.node.Node;
import groowt.view.web.util.SourcePosition;

public class WebViewComponentTemplateCompileException extends ComponentTemplateCompileErrorException {

    private final Object node;

    public WebViewComponentTemplateCompileException(
            String message,
            Class<? extends ViewComponent> forClass,
            Object templateSource,
            Object node
    ) {
        super(message, forClass, templateSource);
        this.node = node;
    }

    public WebViewComponentTemplateCompileException(
            String message,
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource,
            Object node
    ) {
        super(message, cause, forClass, templateSource);
        this.node = node;
    }

    public WebViewComponentTemplateCompileException(
            Throwable cause,
            Class<? extends ViewComponent> forClass,
            Object templateSource,
            Object node
    ) {
        super(cause, forClass, templateSource);
        this.node = node;
    }

    public Object getNode() {
        return this.node;
    }

    @Override
    public String getMessage() {
        if (this.node instanceof Node asNode) {
            final SourcePosition start = asNode.getTokenRange().getStartPosition();
            return "At " + start.toStringLong() + ": " + super.getMessage();
        } else {
            return super.getMessage();
        }
    }

}
