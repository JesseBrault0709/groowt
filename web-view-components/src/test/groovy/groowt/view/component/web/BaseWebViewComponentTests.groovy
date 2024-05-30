package groowt.view.component.web

import groowt.view.component.web.lib.AbstractWebViewComponentTests
import org.junit.jupiter.api.Test

class BaseWebViewComponentTests extends AbstractWebViewComponentTests {

    static final class Greeter extends BaseWebViewComponent {

        final String target

        Greeter(Map<String, Object> attr) {
            super('Hello, $target!')
            this.target = Objects.requireNonNull(attr.get("target"))
        }

    }

    static final class UsingGreeter extends BaseWebViewComponent {

        UsingGreeter() {
            super("<BaseWebViewComponentTests.Greeter target='World' />")
        }

    }

    @Test
    void nestedGreeter() {
        def context = this.context() {
            configureRootScope(WebViewComponentScope) {
                addWithAttr(Greeter)
            }
        }
        this.doTest('<BaseWebViewComponentTests.Greeter target="World" />', 'Hello, World!', context)
    }

    @Test
    void doubleNested() {
        def context = this.context {
            configureRootScope(WebViewComponentScope) {
                addWithAttr(Greeter)
                addWithNoArgConstructor(UsingGreeter)
            }
        }
        this.doTest('<BaseWebViewComponentTests.UsingGreeter />', 'Hello, World!', context)
    }

    @Test
    void closureValueAttrReducedToExpr() {
        def context = this.context {
            configureRootScope(WebViewComponentScope) {
                addWithAttr(Greeter)
                addWithNoArgConstructor(UsingGreeter)
            }
        }
        this.doTest('<BaseWebViewComponentTests.Greeter target={"World"} />', 'Hello, World!', context)
    }

}
