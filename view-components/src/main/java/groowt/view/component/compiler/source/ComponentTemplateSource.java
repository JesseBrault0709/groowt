package groowt.view.component.compiler.source;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.List;

public interface ComponentTemplateSource {

    static ComponentTemplateSource of(String template) {
        return new StringSource(template, null);
    }

    static ComponentTemplateSource of(String template, String name) {
        return new StringSource(template, name);
    }

    static ComponentTemplateSource of(File templateFile) {
        return new FileSource(templateFile);
    }

    static ComponentTemplateSource of(URI templateURI) {
        return new URISource(templateURI);
    }

    static ComponentTemplateSource of(URL url) {
        return new URLSource(url);
    }

    static ComponentTemplateSource of(InputStream templateInputStream) {
        return new InputStreamSource(templateInputStream, null);
    }

    static ComponentTemplateSource of(InputStream templateInputStream, String description) {
        return new InputStreamSource(templateInputStream, description);
    }

    static ComponentTemplateSource of(Reader templateReader) {
        return new ReaderSource(templateReader, null);
    }

    static ComponentTemplateSource of(Reader templateReader, String description) {
        return new ReaderSource(templateReader, description);
    }

    Reader toReader() throws Exception;
    String getDescription();
    boolean canReopen();
    List<String> getLines();

}
