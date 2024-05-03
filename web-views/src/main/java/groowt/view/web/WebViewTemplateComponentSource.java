package groowt.view.web;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public sealed interface WebViewTemplateComponentSource {

    static WebViewTemplateComponentSource of(String template) {
        return new StringSource(template);
    }

    static WebViewTemplateComponentSource of(File templateFile) {
        return new FileSource(templateFile);
    }

    static WebViewTemplateComponentSource of(URI templateURI) {
        return new URISource(templateURI);
    }

    static WebViewTemplateComponentSource of(URL url) {
        return new URLSource(url);
    }

    static WebViewTemplateComponentSource of(InputStream templateInputStream) {
        return new InputStreamSource(templateInputStream);
    }

    static WebViewTemplateComponentSource of(Reader templateReader) {
        return new ReaderSource(templateReader);
    }

    /**
     * @param resourceName An <strong>absolute</strong> path resource name.
     * @return A template source
     */
    static WebViewTemplateComponentSource fromResource(String resourceName) {
        return of(WebViewTemplateComponentSource.class.getClassLoader().getResource(resourceName));
    }

    record StringSource(String template) implements WebViewTemplateComponentSource {}

    record FileSource(File templateFile) implements WebViewTemplateComponentSource {}

    record URISource(URI templateURI) implements WebViewTemplateComponentSource {}

    record URLSource(URL templateURL) implements WebViewTemplateComponentSource {}

    record InputStreamSource(InputStream templateInputStream) implements WebViewTemplateComponentSource {}

    record ReaderSource(Reader templateReader) implements WebViewTemplateComponentSource {}

}
