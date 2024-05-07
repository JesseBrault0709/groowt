package groowt.view.web

import groowt.view.component.factory.ComponentFactoryBase
import groowt.view.web.lib.AbstractWebViewComponentTests
import groowt.view.web.lib.WithContext
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class DefaultWebViewComponentTests extends AbstractWebViewComponentTests {

    private static final class Greeter extends DefaultWebViewComponent {

        private final String target

        Greeter(Map<String, Object> attr) {
            super('Hello, $target!')
            this.target = Objects.requireNonNull(attr.get("target"))
        }

        String getTarget() {
            return this.target
        }

    }

    private static final class GreeterFactory extends ComponentFactoryBase<Greeter> {

        Greeter doCreate(Map<String, Object> attr) {
            return new Greeter(attr)
        }

    }

    private static final class UsingGreeter extends DefaultWebViewComponent {

        UsingGreeter() {
            super("<Greeter target='World' />")
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
            this.configureContext(it)
            currentScope.add('Greeter', new GreeterFactory())
        }
        this.doTest('<Greeter target="World" />', 'Hello, World!', context)
    }

    @Test
    void doubleNested() {
        def context = this.context {
            this.configureContext(it)
            currentScope.add('UsingGreeter') { new UsingGreeter() }
            currentScope.add('Greeter', new GreeterFactory())
        }
        this.doTest('<UsingGreeter />', 'Hello, World!', context)
    }

}
