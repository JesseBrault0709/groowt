package groowt.view;

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.Collections;
import java.util.Map;

public class StandardGStringTemplateView extends GStringTemplateView {

    static {
        final MetaClass mc = new StandardGStringTemplateViewMetaClass();
        mc.initialize();
        GroovySystem.getMetaClassRegistry().setMetaClass(StandardGStringTemplateView.class, mc);
    }

    private final Map<String, Object> locals;
    private final View parent;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public StandardGStringTemplateView(Map<String, Object> args) {
        super(args);
        final Object localsArg = args.get("locals");
        this.locals = localsArg instanceof Map map
                ? Collections.unmodifiableMap(map)
                : localsArg != null
                ? Collections.unmodifiableMap(DefaultGroovyMethods.asType(localsArg, Map.class))
                : Collections.emptyMap();
        this.parent = args.containsKey("parent") ? DefaultGroovyMethods.asType(args.get("parent"), View.class) : null;
    }

    public Map<String, Object> getLocals() {
        return locals;
    }

    public View getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "StandardGStringTemplateView(super: " + super.toString() + ")";
    }

}
