package groowt.view.component.web.lib

import org.junit.jupiter.api.Test

class OutletTests extends AbstractWebViewComponentTests {

    @Test
    void smokeScreen() {
        doTest('<Outlet />', '')
    }

    @Test
    void withChildren() {
        doTest('<Echo items={[0, 1, 2]}><Outlet children={items} /></Echo>', '012')
    }

}
