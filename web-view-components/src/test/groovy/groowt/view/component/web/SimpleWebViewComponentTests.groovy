package groowt.view.component.web

import groowt.view.component.web.lib.AbstractWebViewComponentTests
import org.junit.jupiter.api.Test

class SimpleWebViewComponentTests extends AbstractWebViewComponentTests {

    @Test
    void closureValueWithConstantExpressionEvaluatesToValue() {
        this.doTest('<Echo greeting={"Hello, World!"}>$greeting</Echo>', 'Hello, World!')
    }

    @Test
    void closureValueWithVariableExpressionEvaluatesToValue() {
        this.doTest(
                '<Echo greeting="Hello, World!"><Echo subGreeting={greeting}>$subGreeting</Echo></Echo>',
                'Hello, World!'
        )
    }

    @Test
    void closureWithMethodCallIsClosure() {
        this.doTest(
                '''
                ---
                def helper(String input) {
                    input.capitalize()
                }
                ---
                <Echo subHelper={-> helper('lowercase')}>${subHelper.call()}</Echo>
                '''.stripIndent().trim(), 'Lowercase'
        )
    }

}
