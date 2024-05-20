package groowt.view.component.web.util

import groovy.transform.SelfType
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import groowt.view.component.web.WebViewComponent

@SelfType(WebViewComponent)
trait ConfigurableComponent<T extends WebViewComponent> {

    @SuppressWarnings('GroovyAssignabilityCheck')
    T configure(
            @DelegatesTo(ComponentConfigurator)
            @ClosureParams(value = FromString, options = 'T')
            Closure configure
    ) {
        configure.delegate = new ComponentConfigurator(this)
        configure(this)
        this
    }

}
