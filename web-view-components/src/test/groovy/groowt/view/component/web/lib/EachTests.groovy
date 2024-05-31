package groowt.view.component.web.lib

import org.junit.jupiter.api.Test

class EachTests extends AbstractWebViewComponentTests {

    @Test
    void simple() {
        this.doTest('<Echo items={[0, 1, 2]}><Each items={items} /></Echo>', '012')
    }

    @Test
    void withTransform() {
        this.doTest(
                '<Echo items={[0, 1, 2]}><Each items={items} transform={<p>$it</p>} /></Echo>',
                '<p>0</p><p>1</p><p>2</p>'
        )
    }

}
