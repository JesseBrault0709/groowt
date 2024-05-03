package groowt.view.component;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public interface ComponentTemplateCompiler {
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, File templateFile);
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, String template);
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, URI templateURI);
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, URL templateURL);
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, InputStream inputStream);
    ComponentTemplate compile(Class<? extends ViewComponent> forClass, Reader reader);
}
