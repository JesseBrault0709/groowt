package groowt.view.web.lib


import groowt.view.component.context.ComponentContext
import groowt.view.component.factory.ComponentTemplateSource
import groowt.view.web.compiler.DefaultWebViewComponentTemplateCompiler
import groowt.view.web.compiler.WebViewComponentTemplateCompiler
import groowt.view.web.runtime.DefaultWebViewComponentWriter
import org.codehaus.groovy.control.CompilerConfiguration

import static org.junit.jupiter.api.Assertions.assertEquals

abstract class AbstractWebViewComponentTests implements WithContext {

    protected WebViewComponentTemplateCompiler compiler() {
        new DefaultWebViewComponentTemplateCompiler(
                new GroovyClassLoader(this.class.classLoader),
                CompilerConfiguration.DEFAULT,
                this.class.packageName
        )
    }

    protected void doTest(Reader sourceReader, String expected, ComponentContext context) {
        def template = this.compiler().compileAndGetAnonymous(ComponentTemplateSource.of(sourceReader))
        def renderer = template.getRenderer()
        def sw = new StringWriter()
        def out = new DefaultWebViewComponentWriter(sw)
        renderer.call(context, out)
        assertEquals(expected, sw.toString())
    }

    protected void doTest(String source, String expected, ComponentContext context = this.context()) {
        this.doTest(new StringReader(source), expected, context)
    }

}
