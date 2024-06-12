package groowt.view.component.web.lib

import org.junit.jupiter.api.Test

class SwitchTests extends AbstractWebViewComponentTests {

    @Test
    void smokeScreen() {
        doTest('<Switch></Switch>', '')
    }

    @Test
    void simpleCase() {
        doTest(
                '<Switch item="Hello"><Case match={"Hello"}>Hello, World!</Case></Switch>',
                'Hello, World!'
        )
    }

    @Test
    void multipleCases() {
        doTest('''
                <Switch item='Mars'>
                    <Case match='Jupiter'>Hello, Jupiter!</Case>
                    <Case match='Mars'>Hello, Mars!</Case>
                </Switch>
                '''.trim(),
                'Hello, Mars!'
        )
    }

    @Test
    void withDefaultCase() {
        doTest('''
                <Switch item='Not Mercury'>
                    <Case match='Mercury'>Hello, Mercury!</Case>
                    <DefaultCase>Hello, Not Mercury!</DefaultCase>
                </Switch>
                '''.trim(),
                'Hello, Not Mercury!'
        )
    }

}
