package groowt.view.web.sketching

import groowt.view.web.BaseWebViewComponent

class Greeters {

    static class Simple extends BaseWebViewComponent {

        String target

        Simple(Map attr) {
            super('Hello, $target!')
            this.target = attr.target ?: 'world'
        }

    }

}