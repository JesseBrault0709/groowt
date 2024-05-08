package groowt.view.component.compiler.source;

import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class StringSource implements ComponentTemplateSource {

    private final String template;
    private final @Nullable String name;

    public StringSource(String template, @Nullable String name) {
        this.template = template;
        this.name = name;
    }

    @Override
    public Reader toReader() {
        return new StringReader(this.template);
    }

    @Override
    public String getDescription() {
        return this.name != null ? this.name : "<anonymous string source>";
    }

    @Override
    public boolean canReopen() {
        return true;
    }

    @Override
    public List<String> getLines() {
        return this.template.lines().toList();
    }

}
