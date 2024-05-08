package groowt.view.component.compiler;

import org.codehaus.groovy.tools.GroovyClass;

import java.util.HashSet;
import java.util.Set;

public class SimpleComponentTemplateCompileResult implements ComponentTemplateCompileResult {

    private final GroovyClass templateClass;
    private final Set<GroovyClass> otherClasses;

    public SimpleComponentTemplateCompileResult(
            GroovyClass templateClass,
            Set<GroovyClass> otherClasses
    ) {
        this.templateClass = templateClass;
        this.otherClasses = otherClasses;
    }

    @Override
    public GroovyClass getTemplateClass() {
        return this.templateClass;
    }

    @Override
    public Set<GroovyClass> getOtherClasses() {
        return new HashSet<>(this.otherClasses);
    }

    @Override
    public int hashCode() {
        return this.templateClass.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ComponentTemplateCompileResult other) {
            return this.templateClass.getName().equals(other.getTemplateClass().getName());
        }
        return false;
    }

    @Override
    public String toString() {
        return "sctCompileResult(" + this.templateClass.getName() + ")";
    }

}
