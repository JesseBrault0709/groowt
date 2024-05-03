package groowt.view;

import groovy.lang.Closure;
import groovy.lang.Writable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public interface View {

    /**
     * TODO: consider making this a (package private?) separate class, perhaps with support for GStringTemplateViews, etc.?
     */
    final class ClosureView extends Closure<Object> {

        private final View view;

        public ClosureView(View view) {
            super(view, view);
            this.view = view;
        }

        public void doCall(Writer writer) throws IOException {
            this.view.renderTo(writer);
        }

        public String doCall() {
            return this.view.render();
        }

    }

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

    default Writable asWritable() {
        return writer -> {
            this.renderTo(writer);
            return writer;
        };
    }

    default Closure<Object> asClosure() {
        return new ClosureView(this);
    }

}
