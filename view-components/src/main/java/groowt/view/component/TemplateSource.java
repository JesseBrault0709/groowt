package groowt.view.component;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public sealed interface TemplateSource {

    static TemplateSource of(String template) {
        return new StringSource(template);
    }

    static TemplateSource of(File templateFile) {
        return new FileSource(templateFile);
    }

    static TemplateSource of(URI templateURI) {
        return new URISource(templateURI);
    }

    static TemplateSource of(URL url) {
        return new URLSource(url);
    }

    static TemplateSource of(InputStream templateInputStream) {
        return new InputStreamSource(templateInputStream);
    }

    static TemplateSource of(Reader templateReader) {
        return new ReaderSource(templateReader);
    }

    /**
     * @param resourceName An <strong>absolute</strong> path resource name.
     * @return A template source
     */
    static TemplateSource fromResource(String resourceName) {
        return of(TemplateSource.class.getClassLoader().getResource(resourceName));
    }

    record StringSource(String template) implements TemplateSource {}

    record FileSource(File templateFile) implements TemplateSource {}

    record URISource(URI templateURI) implements TemplateSource {}

    record URLSource(URL templateURL) implements TemplateSource {}

    record InputStreamSource(InputStream templateInputStream) implements TemplateSource {}

    record ReaderSource(Reader templateReader) implements TemplateSource {}

}
