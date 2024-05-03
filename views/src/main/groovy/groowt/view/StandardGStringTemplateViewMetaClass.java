package groowt.view;

import groovy.lang.*;
import org.codehaus.groovy.runtime.metaclass.MethodSelectionException;
import org.codehaus.groovy.util.FastArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StandardGStringTemplateViewMetaClass extends ExpandoMetaClass {

    private static final Logger logger = LoggerFactory.getLogger(StandardGStringTemplateViewMetaClass.class);

    private static StandardGStringTemplateView asView(Object object) {
        return object instanceof StandardGStringTemplateView view ? view : null;
    }

    private static Object[] asArgsArray(Object object) {
        return object instanceof Object[] objects ? objects : new Object[] { object };
    }

    private static MetaMethod findMetaMethod(MetaClass metaClass, String methodName, Object[] argsArray) {
        final List<MetaMethod> metaMethods = metaClass.getMetaMethods().stream().filter(metaMethod ->
                metaMethod.getName().equals(methodName) && metaMethod.isValidMethod(argsArray)
        ).toList();
        if (metaMethods.size() > 1) {
            @SuppressWarnings("rawtypes")
            final Class[] argTypes = Arrays.stream(argsArray).map(Object::getClass).toArray(Class[]::new);
            throw new MethodSelectionException(methodName, new FastArray(metaMethods), argTypes);
        } else if (metaMethods.size() == 1) {
            return metaMethods.getFirst();
        } else {
            return null;
        }
    }

    private static void warnWrongType(Object object) {
        logger.warn(
                "StandardGStringTemplateViewMetaClass should only be used as a MetaClass of StandardGStringTemplateViewMetaClass or a subclass thereof; given "
                        + object + " of type " + object.getClass()
        );
    }

    private static MetaClass findMetaClass(Object object) {
        return switch (object) {
            case GroovyObject groovyObject -> groovyObject.getMetaClass();
            default -> GroovySystem.getMetaClassRegistry().getMetaClass(object.getClass());
        };
    }

    public StandardGStringTemplateViewMetaClass() {
        super(StandardGStringTemplateView.class, true, true);
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object arguments) {
        final StandardGStringTemplateView view = asView(object);
        if (view == null) {
            logger.warn("StandardGStringTemplateViewMetaClass should only be used as a MetaClass of StandardGStringTemplateViewMetaClass.");
            return super.invokeMethod(object, methodName, arguments);
        }

        // local closure
        final Map<String, Object> locals = view.getLocals();
        if (locals.containsKey(methodName)) {
            final Object local = locals.get(methodName);
            if (local instanceof @SuppressWarnings("rawtypes") Closure closure) {
                return closure.call(arguments);
            }
        }

        // for both self and parent
        final Object[] argsArray = asArgsArray(arguments);

        // self
        final MetaMethod selfMethod = findMetaMethod(this, methodName, argsArray);
        if (selfMethod != null) {
            return selfMethod.invoke(view, argsArray);
        }

        // parent hierarchy
        View parent = view.getParent();
        while (parent != null) {
            final var parentMetaMethod = findMetaMethod(findMetaClass(parent), methodName, argsArray);
            if (parentMetaMethod != null) {
                return parentMetaMethod.invoke(parent, argsArray);
            } else if (parent instanceof StandardGStringTemplateView) {
                parent = ((StandardGStringTemplateView) parent).getParent();
            } else {
                parent = null;
            }
        }

        return super.invokeMethod(object, methodName, arguments);
    }

    @Override
    public Object getProperty(Object object, String name) {
        final StandardGStringTemplateView view = asView(object);
        if (view == null) {
            warnWrongType(object);
            return super.getProperty(object, name);
        }

        // local
        final Map<String, Object> locals = view.getLocals();
        if (locals != null && locals.containsKey(name)) {
            return view.getLocals().get(name);
        }

        // self
        final var metaProperty = this.getMetaProperty(name);
        if (metaProperty != null) {
            return metaProperty.getProperty(object);
        }

        // parent hierarchy
        View parent = view.getParent();
        while (parent != null) {
            final var parentMetaProperty = findMetaClass(parent).getMetaProperty(name);
            if (parentMetaProperty != null) {
                return parentMetaProperty.getProperty(parent);
            } else if (parent instanceof StandardGStringTemplateView) {
                parent = ((StandardGStringTemplateView) parent).getParent();
            } else {
                parent = null;
            }
        }

        // all else fails, try super metaClass
        return super.getProperty(object, name);
    }

    @Override
    public void setProperty(Object object, String name, Object value) {
        final StandardGStringTemplateView view = asView(object);
        if (view == null) {
            warnWrongType(object);
            super.setProperty(object, name, value);
            return;
        }

        // local
        final Map<String, Object> locals = view.getLocals();
        if (locals != null && locals.containsKey(name)) {
            locals.put(name, value);
            return;
        }

        // self
        final var selfMetaProperty = this.getMetaProperty(name);
        if (selfMetaProperty != null) {
            selfMetaProperty.setProperty(view, value);
            return;
        }

        // parent hierarchy
        View parent = view.getParent();
        while (parent != null) {
            final var parentMetaProperty = findMetaClass(parent).getMetaProperty(name);
            if (parentMetaProperty != null) {
                parentMetaProperty.setProperty(parent, value);
                return;
            } else if (parent instanceof StandardGStringTemplateView) {
                parent = ((StandardGStringTemplateView) parent).getParent();
            } else {
                parent = null;
            }
        }

        // all else fails, try super metaClass
        super.setProperty(object, name, value);
    }

}
