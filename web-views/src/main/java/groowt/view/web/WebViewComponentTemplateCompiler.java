package groowt.view.web;

import groowt.view.component.ComponentTemplate;
import groowt.view.component.ComponentTemplateCompiler;

import java.io.Reader;

public interface WebViewComponentTemplateCompiler extends ComponentTemplateCompiler {
    ComponentTemplate compileAnonymous(Reader reader);
}
