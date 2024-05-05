package groowt.view;

import groovy.lang.Writable;
import groovy.text.Template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class GroovyTemplateView implements View {

    private final Template template;

    public GroovyTemplateView(Template template) {
        this.template = template;
    }

    @Override
    public void renderTo(Writer writer) throws IOException {
        this.getWritableFrom(this.template).writeTo(writer);
    }

    protected Writable getWritableFrom(Template template) {
        return template.make(this.asType(Map.class));
    }

}
