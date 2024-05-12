package groowt.view.web.lib

import groowt.view.web.BaseWebViewComponent
import groowt.view.web.DefaultWebViewComponentScope
import groowt.view.web.WebViewComponentContext
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
        context.getRootScope(DefaultWebViewComponentScope).with {
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
