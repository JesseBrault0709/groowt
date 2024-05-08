package groowt.view.component.compiler.source;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.List;

public class URISource implements ComponentTemplateSource {

    private final URI templateURI;
    private List<String> lines;

    public URISource(URI templateURI) {
        this.templateURI = templateURI;
    }

    @Override
    public Reader toReader() throws Exception {
        return new InputStreamReader(this.templateURI.toURL().openStream());
    }

    @Override
    public String getDescription() {
        return this.templateURI.toString();
    }

    @Override
    public boolean canReopen() {
        return true;
    }

    public URI getURI() {
        return this.templateURI;
    }

    @Override
    public List<String> getLines() {
        if (this.lines == null) {
            try (final var inputStream = this.templateURI.toURL().openStream()) {
                final String allSource = new String(inputStream.readAllBytes());
                this.lines = allSource.lines().toList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.lines;
    }

}
