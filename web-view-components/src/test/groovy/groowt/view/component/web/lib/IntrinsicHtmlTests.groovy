package groowt.view.component.web.lib


import org.junit.jupiter.api.Test

class IntrinsicHtmlTests extends AbstractWebViewComponentTests {

    @Test
    void simplePElement() {
        this.doTest('<p>Hello, World!</p>', '<p>Hello, World!</p>')
    }

    @Test
    void h1Element() {
        this.doTest('<h1>Hello!</h1>', '<h1>Hello!</h1>')
    }

    @Test
    void attrTransferred() {
        this.doTest('<h1 class="my-heading">Hello!</h1>', '<h1 class="my-heading">Hello!</h1>')
    }

    @Test
    void canUseEchoAttrPropertyViaContext() {
        this.doTest('<Echo greeting="Hello!"><p>$greeting</p></Echo>', '<p>Hello!</p>')
    }

}
