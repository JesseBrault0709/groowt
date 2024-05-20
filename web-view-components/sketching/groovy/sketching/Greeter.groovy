package sketching

import groowt.view.web.BaseWebViewComponent

class Greeter extends BaseWebViewComponent {

    String target

    Greeter(Map attr) {
        super('Hello, $target!')
        this.target = attr.target ?: 'World'
    }

}