package groowt.gradle;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface TemplateSourceSet {

    TemplateSourceDirectorySet getTemplates();

    SourceDirectorySet getAllTemplates();

    TemplateSourceSet templates(Action<? super TemplateSourceDirectorySet> action);

    @SuppressWarnings("rawtypes")
    TemplateSourceSet templates(Closure closure);

}
