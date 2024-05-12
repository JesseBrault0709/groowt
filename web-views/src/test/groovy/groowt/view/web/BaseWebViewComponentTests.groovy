package groowt.view.web

import groowt.view.component.factory.ComponentFactories
import groowt.view.web.lib.AbstractWebViewComponentTests
import org.junit.jupiter.api.Test

class BaseWebViewComponentTests extends AbstractWebViewComponentTests {

    static final class Greeter extends BaseWebViewComponent {

        private final String target

        Greeter(Map<String, Object> attr) {
            super('Hello, $target!')
            this.target = Objects.requireNonNull(attr.get("target"))
        }

        String getTarget() {
            return this.target
        }

    }

    static final class UsingGreeter extends BaseWebViewComponent {

        UsingGreeter() {
            super("<BaseWebViewComponentTests.Greeter target='World' />")
        }

    }

    @Test
    void withPreambleImport() {
        this.doTest('''
            ---
            import groovy.transform.Field
            
            @Field
            String greeting = 'Hello, World!'
            ---
            $greeting
            '''.stripIndent().trim(), "Hello, World!")
    }

    @Test
    void nestedGreeter() {
        def context = this.context {
            getRootScope(DefaultWebViewComponentScope).with {
                addWithAttr(Greeter)
            }
        }
        this.doTest('<BaseWebViewComponentTests.Greeter target="World" />', 'Hello, World!', context)
    }

    @Test
    void doubleNested() {
        def context = this.context {
            getRootScope(DefaultWebViewComponentScope).with {
                addWithAttr(Greeter)
                add(UsingGreeter, ComponentFactories.ofSupplier { new UsingGreeter() })
            }
        }
        this.doTest('<BaseWebViewComponentTests.UsingGreeter />', 'Hello, World!', context)
    }

}
