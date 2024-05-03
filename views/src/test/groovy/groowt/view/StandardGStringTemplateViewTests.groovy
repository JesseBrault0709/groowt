package groowt.view

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class StandardGStringTemplateViewTests {

    @Test
    void smokeScreen() {
        def view = new StandardGStringTemplateView(src: '<%= "hello" %>')
        assertEquals('hello', view.render())
    }

    @Test
    void fetchesLocal() {
        def view = new StandardGStringTemplateView(src: '<%= greeting %>', locals: [greeting: 'hello'])
        assertEquals('hello', view.render())
    }

    private static final class GreetingView extends StandardGStringTemplateView {

        final String greeting = 'hello'

        GreetingView() {
            super(src: '<%= greeting %>')
        }

    }

    @Test
    void fetchesFromSelf() {
        def view = new GreetingView()
        assertEquals('hello', view.render())
    }

    @Test
    void fetchesFromParent() {
        def parent = new GreetingView()
        def view = new StandardGStringTemplateView(src: '<%= greeting %>', parent: parent)
        assertEquals('hello', view.render())
    }

}
