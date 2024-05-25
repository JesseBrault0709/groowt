package groowt.view.component.compiler.source;

import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.List;

public class ReaderSource implements ComponentTemplateSource {

    private final Reader reader;
    private final @Nullable String description;

    public ReaderSource(Reader reader, @Nullable String description) {
        this.reader = reader;
        this.description = description;
    }

    @Override
    public Reader toReader() {
        return this.reader;
    }

    @Override
    public String getDescriptiveName() {
        return this.description != null ? this.description : "<anonymous Reader source>";
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
