package groowt.view.component.compiler;

import groowt.view.component.ViewComponent;

import java.util.HashMap;
import java.util.Map;

public abstract class CachingComponentTemplateCompiler<U extends ComponentTemplateCompileUnit>
        implements ComponentTemplateCompiler<U> {

    private final Map<Class<? extends ViewComponent>, ComponentTemplateCompileResult> cache = new HashMap<>();

//    private ComponentTemplate instantiate(
//            GroovyClassLoader groovyClassLoader,
//            CompileResult compileResult
//    ) {
//        for (final var groovyClass : compileResult.otherClasses()) {
//            // Try to find it. If we can't, we need to load it via the groovy loader
//            try {
//                Class.forName(groovyClass.getName(), true, groovyClassLoader);
//            } catch (ClassNotFoundException ignored) {
//                groovyClassLoader.defineClass(groovyClass.getName(), groovyClass.getBytes());
//            } catch (LinkageError ignored) {
//                // no-op, because we already have it
//            }
//        }
//        final GroovyClass templateGroovyClass = compileResult.templateClass();
//        Class<?> templateClass;
//        // Try to find it. If we can't, we need to load it via the groovy loader
//        try {
//            templateClass = Class.forName(templateGroovyClass.getName(), true, groovyClassLoader);
//        } catch (ClassNotFoundException ignored) {
//            templateClass = groovyClassLoader.defineClass(
//                    templateGroovyClass.getName(),
//                    templateGroovyClass.getBytes()
//            );
//        }
//        try {
//            return (ComponentTemplate) templateClass.getConstructor().newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException("Unable to instantiate ComponentTemplate " + templateClass.getName(), e);
//        }
//    }

    @Override
    public final ComponentTemplateCompileResult compile(U compileUnit)
            throws ComponentTemplateCompileException {
        if (this.cache.containsKey(compileUnit.getForClass())) {
            return this.cache.get(compileUnit.getForClass());
        } else {
            final ComponentTemplateCompileResult compileResult = this.doCompile(compileUnit);
            this.cache.put(compileUnit.getForClass(), compileResult);
            return compileResult;
        }
    }

    protected abstract ComponentTemplateCompileResult doCompile(U compileUnit) throws ComponentTemplateCompileException;

}
