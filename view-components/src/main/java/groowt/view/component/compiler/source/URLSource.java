package groowt.view.component.compiler.source;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class URLSource implements ComponentTemplateSource {

    private final URL url;
    private List<String> lines;

    public URLSource(URL url) {
        this.url = url;
    }

    @Override
    public Reader toReader() throws Exception {
        return new InputStreamReader(this.url.openStream());
    }

    @Override
    public String getDescriptiveName() {
        return this.url.toString();
    }

    @Override
    public boolean canReopen() {
        return true;
    }

    public @Nullable URI getURI() {
        try {
            return this.url.toURI();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public List<String> getLines() {
        if (this.lines == null) {
            try (final var inputStream = this.url.openStream()) {
                final String allSource = new String(inputStream.readAllBytes());
                this.lines = allSource.lines().toList();
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
        return this.lines;
    }

}
