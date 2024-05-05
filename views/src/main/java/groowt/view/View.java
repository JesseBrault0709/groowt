package groowt.view;

import groovy.lang.Closure;
import groovy.lang.Writable;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

@FunctionalInterface
public interface View {

    void renderTo(Writer writer) throws IOException;

    default String render() {
        final Writer w = new StringWriter();
        try {
            this.renderTo(w);
            return w.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    default Closure asWritable() {
        if (this instanceof Closure) {
            return (Closure) this;
        } else {
            return new WritableClosureView(this);
        }
    }

    @SuppressWarnings("rawtypes")
    default Closure asClosure() {
        if (this instanceof Closure) {
            return (Closure) this;
        } else {
            return new WritableClosureView(this);
        }
    }

    @SuppressWarnings("unchecked")
    default <T> T asType(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            return clazz.cast(this);
        } else if (clazz.equals(Writable.class)) {
            return (T) this.asWritable();
        } else if (clazz.equals(Closure.class)) {
            return (T) this.asClosure();
        } else if (clazz.equals(String.class)) {
            return (T) this.render();
        } else {
            return DefaultGroovyMethods.asType(this, clazz);
        }
    }

}
