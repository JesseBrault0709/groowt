package groowt.view.component.web.lib

import org.junit.jupiter.api.Test

class RenderTests extends AbstractWebViewComponentTests {

    static final class RenderToExample {

        void renderTo(Writer w) {
            w << 'Hello, World!'
        }

    }

    static final class RenderExample {

        String render() {
            'Hello, World!'
        }

    }

    @Test
    void renderToExample() {
        doTest('''
                ---
                package groowt.view.component.web.lib
                ---
                <Render item={new RenderTests.RenderToExample()} />
                '''.stripIndent().trim(),
                'Hello, World!'
        )
    }

    @Test
    void renderExample() {
        doTest('''
                ---
                package groowt.view.component.web.lib
                ---
                <Render item={new RenderTests.RenderExample()} />
                '''.stripIndent().trim(),
                'Hello, World!'
        )
    }

}
