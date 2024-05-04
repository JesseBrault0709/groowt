package groowt.view.web.lib

import groowt.view.web.DefaultWebViewComponentContext
import org.junit.jupiter.api.Test

class EchoTests extends AbstractWebViewComponentTests {

    @Test
    void typeOnlySelfClose() {
        def context = new DefaultWebViewComponentContext()
        context.pushDefaultScope()
        context.currentScope.add('Echo', new Echo.EchoFactory())
        this.doTest('<Echo(true) />', '<Echo />', context)
    }

}
