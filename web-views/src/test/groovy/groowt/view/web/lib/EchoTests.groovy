package groowt.view.web.lib

import groowt.view.web.WebViewComponentContext
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class EchoTests extends AbstractWebViewComponentTests {

    @Override
    void configureContext(WebViewComponentContext context) {
        super.configureContext(context)
        context.currentScope.add('Echo', Echo.FACTORY)
    }

    @Test
    void selfClose() {
        this.doTest('<Echo />', '<Echo />')
    }

    @Test
    void noSelfClose() {
        this.doTest('<Echo(false) />', '<Echo></Echo>')
    }

    @Test
    @Disabled("Not possible to render children directly to a writer yet.")
    void withChildren() {
        this.doTest('<Echo>Hello, World!</Echo>', '<Echo>Hello, World!</Echo>')
    }

}
