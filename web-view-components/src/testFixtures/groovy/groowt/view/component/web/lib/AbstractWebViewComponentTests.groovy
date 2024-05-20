package groowt.view.component.web.lib

import groowt.view.component.compiler.SimpleComponentTemplateClassFactory
import groowt.view.component.compiler.source.ComponentTemplateSource
import groowt.view.component.context.ComponentContext
import groowt.view.component.runtime.DefaultComponentWriter
import groowt.view.component.web.compiler.WebViewComponentTemplateCompiler

import static org.junit.jupiter.api.Assertions.assertEquals

abstract class AbstractWebViewComponentTests implements WithContext {

    protected void doTest(Reader sourceReader, String expected, ComponentContext context) {
        def compileResult = WebViewComponentTemplateCompiler.get()
                .compileAnonymous(ComponentTemplateSource.of(sourceReader), this.class.packageName)

        def factory = new SimpleComponentTemplateClassFactory()
        def templateClass = factory.getTemplateClass(compileResult)

        def renderer = templateClass.getConstructor().newInstance().getRenderer()

        def sw = new StringWriter()
        def out = new DefaultComponentWriter(sw)
        renderer.call(context, out)
        assertEquals(expected, sw.toString())
    }

    protected void doTest(String source, String expected, ComponentContext context = this.context()) {
        this.doTest(new StringReader(source), expected, context)
    }

}
