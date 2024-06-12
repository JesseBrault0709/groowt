package groowt.view.component.web.lib

import org.junit.jupiter.api.Test

class WhenNotEmptyTests extends AbstractWebViewComponentTests {

    @Test
    void emptyCollection() {
        doTest('<WhenNotEmpty items={[]}>Hello, World!</WhenNotEmpty>', '')
    }

    @Test
    void emptyMap() {
        doTest('<WhenNotEmpty items={[:]}>Hello, World!</WhenNotEmpty>', '')
    }

    @Test
    void nonEmptyCollection() {
        doTest('<WhenNotEmpty items={[0, 1, 2]}>012</WhenNotEmpty>', '012')
    }

    @Test
    void nonEmptyMap() {
        doTest('<WhenNotEmpty items={[a: 0]}>a: 0</WhenNotEmpty>', 'a: 0')
    }

}
