package groowt.view.web.lib


import org.junit.jupiter.api.Test

class EchoTests extends AbstractWebViewComponentTests {

    @Test
    void selfClose() {
        this.doTest('<Echo />', '')
    }

    @Test
    void noSelfClose() {
        this.doTest('<Echo></Echo>', '')
    }

    @Test
    void withChildren() {
        this.doTest('<Echo>Hello, World!</Echo>', 'Hello, World!')
    }

}
