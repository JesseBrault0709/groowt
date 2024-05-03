package groowt.view.component;

import groovy.lang.Closure;

public interface ComponentTemplate {
    Closure<?> getRenderer();
}
