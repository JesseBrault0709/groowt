package groowt.gradle;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;

import javax.inject.Inject;

public class DefaultTemplateSourceDirectorySet extends DefaultSourceDirectorySet implements TemplateSourceDirectorySet {

    @Inject
    public DefaultTemplateSourceDirectorySet(
            SourceDirectorySet delegate,
            TaskDependencyFactory taskDependencyFactory
    ) {
        super(delegate, taskDependencyFactory);
    }

}
