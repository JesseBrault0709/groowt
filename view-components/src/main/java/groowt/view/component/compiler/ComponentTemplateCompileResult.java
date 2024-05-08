package groowt.view.component.compiler;

import org.codehaus.groovy.tools.GroovyClass;

import java.util.Set;

public interface ComponentTemplateCompileResult {
    GroovyClass getTemplateClass();
    Set<GroovyClass> getOtherClasses();
}
