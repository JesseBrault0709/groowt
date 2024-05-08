package groowt.view.web.runtime;

import groovy.lang.Closure;
import groowt.view.component.ComponentTemplate;

public class WebViewComponentChildCollectorClosure extends Closure<Object> {

    public static Closure<?> get(ComponentTemplate template, Closure<?> collectorClosure) {
        return new WebViewComponentChildCollectorClosure(template, collectorClosure);
    }

    private final ComponentTemplate template;
    private final Closure<?> collectorClosure;

    private WebViewComponentChildCollectorClosure(ComponentTemplate template, Closure<?> collectorClosure) {
        super(template, template);
        this.template = template;
        this.collectorClosure = collectorClosure;
    }

    public ComponentTemplate getTemplate() {
        return this.template;
    }

    public Object doCall(WebViewComponentChildCollector childCollector) {
        return this.collectorClosure.call(childCollector);
    }

}
