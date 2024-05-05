package groowt.view.web

import groowt.view.component.factory.ComponentFactoryBase
import groowt.view.web.lib.WithContext
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class DefaultWebViewComponentTests implements WithContext {

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
        def c = new DefaultWebViewComponent(
            '''
            ---
            import groovy.transform.Field
            
            @Field
            String greeting = 'Hello, World!'
            ---
            $greeting
            '''.stripIndent().trim()
        )
        c.context = this.context()
        assertEquals("Hello, World!", c.render())
    }

    @Test
    void nestedGreeter() {
        def context = this.context {
            this.configureContext(it)
            currentScope.add('Greeter', new GreeterFactory())
        }
        def c = new DefaultWebViewComponent('<Greeter target="World" />')
        c.context = context
        assertEquals('Hello, World!', c.render())
    }

    @Test
    void doubleNested() {
        def context = this.context {
            this.configureContext(it)
            currentScope.add('UsingGreeter') { new UsingGreeter() }
            currentScope.add('Greeter', new GreeterFactory())
        }
        def c = new DefaultWebViewComponent('<UsingGreeter />')
        c.context = context
        assertEquals('Hello, World!', c.render())
    }

}
