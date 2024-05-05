package groowt.view;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaProperty;
import groovy.lang.Writable;
import org.codehaus.groovy.runtime.typehandling.GroovyCastException;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO: create a Map implementation that can access the View for keys/values, or do a metaclass thing
 * TODO: get rid of this and just move the asType stuff to GStringTemplateView
 */
public abstract class AbstractView extends GroovyObjectSupport implements View {

    @SuppressWarnings("unchecked")
    public <T> T asType(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return (T) this;
        } else if (clazz.equals(Writable.class)) {
            return (T) this.asWritable();
        } else if (clazz.equals(Closure.class)) {
            return (T) this.asClosure();
        } else if (clazz.equals(Map.class)) {
            return (T) this.getMetaClass().getProperties().stream()
                    .collect(Collectors.toMap(MetaProperty::getName, metaProperty -> metaProperty.getProperty(this)));
        } else {
            throw new GroovyCastException(this, clazz);
        }
    }

}
