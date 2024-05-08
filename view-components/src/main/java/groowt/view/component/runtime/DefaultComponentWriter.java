package groowt.view.component.runtime;

import groovy.lang.GString;
import groowt.view.component.ComponentRenderException;
import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public class DefaultComponentWriter implements ComponentWriter {

    private final Writer delegate;
    private RenderContext renderContext;
    private ComponentContext componentContext;

    public DefaultComponentWriter(Writer delegate) {
        this.delegate = delegate;
    }

    protected RenderContext getRenderContext() {
        return Objects.requireNonNull(this.renderContext);
    }

    @Override
    public void setRenderContext(RenderContext renderContext) {
        this.renderContext = Objects.requireNonNull(renderContext);
    }

    protected ComponentContext getComponentContext() {
        return this.componentContext;
    }

    @Override
    public void setComponentContext(ComponentContext componentContext) {
        this.componentContext = Objects.requireNonNull(componentContext);
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

    private void doComponentRender(ViewComponent viewComponent) throws IOException {
        this.getRenderContext().pushComponent(viewComponent);
        this.getComponentContext().pushDefaultScope();
        viewComponent.renderTo(this.delegate);
        this.getComponentContext().popScope();
        this.getRenderContext().popComponent(viewComponent);
    }

    @Override
    public void append(ViewComponent viewComponent) {
        try {
            this.doComponentRender(viewComponent);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (ComponentRenderException componentRenderException) {
            throw componentRenderException;
        } catch (Exception exception) {
            throw new ComponentRenderException(viewComponent, exception);
        }
    }

    @Override
    public void append(ViewComponent viewComponent, int line, int column) {
        try {
            this.doComponentRender(viewComponent);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (ComponentRenderException componentRenderException) {
            throw componentRenderException;
        } catch (Exception exception) {
            throw new ComponentRenderException(viewComponent, line, column, exception);
        }
    }

    @Override
    public void append(Object object) {
        switch (object) {
            case String s -> this.append(s);
            case GString gString -> this.append(gString);
            case ViewComponent viewComponent -> this.append(viewComponent);
            default -> {
                try {
                    this.delegate.append(object.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
