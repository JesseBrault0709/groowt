package groowt.view.component.factory;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public sealed interface ComponentTemplateSource {

    static ComponentTemplateSource of(String template) {
        return new StringSource(template);
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
        return new InputStreamSource(templateInputStream);
    }

    static ComponentTemplateSource of(Reader templateReader) {
        return new ReaderSource(templateReader);
    }

    /**
     * @param resourceName An <strong>absolute</strong> path resource name.
     * @return A template source
     */
    static ComponentTemplateSource fromResource(String resourceName) {
        return of(ComponentTemplateSource.class.getClassLoader().getResource(resourceName));
    }

    record StringSource(String template) implements ComponentTemplateSource {}

    record FileSource(File templateFile) implements ComponentTemplateSource {}

    record URISource(URI templateURI) implements ComponentTemplateSource {}

    record URLSource(URL templateURL) implements ComponentTemplateSource {}

    record InputStreamSource(InputStream templateInputStream) implements ComponentTemplateSource {}

    record ReaderSource(Reader templateReader) implements ComponentTemplateSource {}

}
