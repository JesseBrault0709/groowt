package groowt.view.web.runtime;

import groovy.lang.GString;
import groowt.view.component.ComponentRenderException;
import groowt.view.component.ViewComponent;

import java.io.IOException;
import java.io.Writer;

public class WebViewComponentWriter {

    private final Writer delegate;

    public WebViewComponentWriter(Writer delegate) {
        this.delegate = delegate;
    }

    public void append(String string) {
        try {
            this.delegate.append(string);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public void append(GString gString) {
        final String content;
        try {
            content = gString.toString();
        } catch (Exception exception) {
            throw new ComponentRenderException(exception);
        }
        try {
            this.delegate.append(content);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public void append(GString gString, int line, int column) {
        final String content;
        try {
            content = gString.toString();
        } catch (Exception exception) {
            throw new ComponentRenderException(line, column, exception);
        }
        try {
            this.delegate.append(content);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public void append(ViewComponent viewComponent) {
        try {
            viewComponent.renderTo(this.delegate);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (Exception exception) {
            throw new ComponentRenderException(viewComponent, exception);
        }
    }

    public void append(ViewComponent viewComponent, int line, int column) {
        try {
            viewComponent.renderTo(this.delegate);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (Exception exception) {
            throw new ComponentRenderException(viewComponent, line, column, exception);
        }
    }

    public void append(Object object) {
        try {
            this.delegate.append(object.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
