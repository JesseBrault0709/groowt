package groowt.view.component.compiler;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.factory.ComponentTemplateSource;
import groowt.view.component.factory.ComponentTemplateSource.*;

import java.io.*;
import java.net.URI;
import java.net.URL;

public abstract class AbstractComponentTemplateCompiler implements ComponentTemplateCompiler {

    protected abstract ComponentTemplate compile(
            ComponentTemplateSource componentTemplateSource,
            Class<? extends ViewComponent> forClass,
            Reader actualSource
    );

    @Override
    public ComponentTemplate compile(Class<? extends ViewComponent> forClass, ComponentTemplateSource source) {
        return switch (source) {
            case FileSource(File file) -> {
                try {
                    yield this.compile(source, forClass, new FileReader(file));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case StringSource(String rawSource) ->
                    this.compile(source, forClass, new StringReader(rawSource));
            case InputStreamSource(InputStream inputStream) ->
                    this.compile(source, forClass, new InputStreamReader(inputStream));
            case URISource(URI uri) -> {
                try {
                    yield this.compile(source, forClass, new InputStreamReader(uri.toURL().openStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case URLSource(URL url) -> {
                try {
                    yield this.compile(source, forClass, new InputStreamReader(url.openStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case ReaderSource(Reader reader) -> this.compile(source, forClass, reader);
        };
    }

}
