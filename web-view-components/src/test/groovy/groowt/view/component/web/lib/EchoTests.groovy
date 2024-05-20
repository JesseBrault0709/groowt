package groowt.view.component.web.lib

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

    @Test
    void childrenCanUseProperties() {
        this.doTest('<Echo greeting="Hello, World!">$greeting</Echo>', 'Hello, World!')
    }

}
