package groowt.view.web.runtime;

import groovy.lang.GString;
import groowt.view.component.ComponentRenderException;
import groowt.view.component.ViewComponent;

import java.io.IOException;
import java.io.Writer;

public class DefaultWebViewComponentWriter implements WebViewComponentWriter {

    private final Writer delegate;

    public DefaultWebViewComponentWriter(Writer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void append(String string) {
        try {
            this.delegate.append(string);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public void append(GString gString) {
        try {
            gString.writeTo(this.delegate);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (Exception exception) {
            throw new ComponentRenderException(exception);
        }
    }

    @Override
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

    @Override
    public void append(ViewComponent viewComponent) {
        try {
            viewComponent.renderTo(this.delegate);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (Exception exception) {
            throw new ComponentRenderException(viewComponent, exception);
        }
    }

    @Override
    public void append(ViewComponent viewComponent, int line, int column) {
        try {
            viewComponent.renderTo(this.delegate);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (Exception exception) {
            throw new ComponentRenderException(viewComponent, line, column, exception);
        }
    }

    @Override
    public void append(Object object) {
        try {
            this.delegate.append(object.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leftShift(Object object) {
        switch (object) {
            case String s -> this.append(s);
            case GString gs -> this.append(gs);
            case ViewComponent viewComponent -> this.append(viewComponent);
            default -> this.append(object);
        }
    }

}
