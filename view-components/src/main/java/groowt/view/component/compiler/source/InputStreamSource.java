package groowt.view.component.compiler.source;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class InputStreamSource implements ComponentTemplateSource {

    private final InputStream templateInputStream;
    private final @Nullable String description;

    public InputStreamSource(InputStream templateInputStream, @Nullable String description) {
        this.templateInputStream = templateInputStream;
        this.description = description;
    }

    @Override
    public Reader toReader() {
        return new InputStreamReader(this.templateInputStream);
    }

    @Override
    public String getDescription() {
        return this.description != null ? this.description : "<anonymous InputStream source>";
    }

    @Override
    public boolean canReopen() {
        return false;
    }

    @Override
    public List<String> getLines() {
        throw new UnsupportedOperationException();
    }

}
