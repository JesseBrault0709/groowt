package groowt.view.component.web.lib

import groowt.view.component.web.BaseWebViewComponent
import groowt.view.component.web.WebViewComponentContext
import groowt.view.component.web.WebViewComponentScope
import org.junit.jupiter.api.Test

class FragmentTests extends AbstractWebViewComponentTests {

    static class Greeter extends BaseWebViewComponent {

        String greeting

        Greeter(Map<String, Object> attr) {
            super('$greeting')
            greeting = attr.greeting
        }

    }

    @Override
    void configureContext(WebViewComponentContext context) {
        context.configureRootScope(WebViewComponentScope) {
            addWithAttr(Greeter)
        }
    }

    @Test
    void simple() {
        this.doTest('<><FragmentTests.Greeter greeting="Hello, World!" /></>', 'Hello, World!')
    }

    @Test
    void multipleChildren() {
        this.doTest(
                '''
                <>
                    <FragmentTests.Greeter greeting='Hello, one!' />&nbsp;<FragmentTests.Greeter greeting='Hello, two!' />
                </>
                '''.stripIndent(), 'Hello, one!&nbsp;Hello, two!'
        )
    }

}
