package groowt.view;

import groovy.lang.Closure;
import groovy.lang.Writable;

import java.io.IOException;
import java.io.Writer;

final class WritableClosureView extends Closure<Object> implements View, Writable {

    private final View view;

    public WritableClosureView(View view) {
        super(view, view);
        this.view = view;
    }

    @Override
    public void renderTo(Writer writer) throws IOException {
        this.view.renderTo(writer);
    }

    public void doCall(Writer writer) throws IOException {
        this.view.renderTo(writer);
    }

    public String doCall() {
        return this.view.render();
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        this.view.renderTo(out);
        return out;
    }

}
