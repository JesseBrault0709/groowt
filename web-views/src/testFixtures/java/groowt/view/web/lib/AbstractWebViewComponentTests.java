package groowt.view.web.lib;

import groovy.lang.Closure;
import groowt.view.component.ComponentContext;
import groowt.view.component.ComponentTemplate;
import groowt.view.web.DefaultWebComponentTemplateCompiler;
import groowt.view.web.DefaultWebViewComponentContext;
import groowt.view.web.runtime.WebViewComponentWriter;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractWebViewComponentTests {

    protected void doTest(Reader source, String expected, ComponentContext context) {
        final var compiler = new DefaultWebComponentTemplateCompiler(
                CompilerConfiguration.DEFAULT, this.getClass().getPackageName()
        );
        final ComponentTemplate template = compiler.compileAnonymous(source);
        final Closure<?> renderer = template.getRenderer();
        final StringWriter sw = new StringWriter();
        final WebViewComponentWriter out = new WebViewComponentWriter(sw);
        renderer.call(context, out);
        assertEquals(expected, sw.toString().trim());
    }

    protected void doTest(String source, String expected, ComponentContext context) {
        this.doTest(new StringReader(source), expected, context);
    }

    protected void doTest(String source, String expected) {
        final var context = new DefaultWebViewComponentContext();
        context.pushDefaultScope();
        this.doTest(source, expected, context);
    }

}
