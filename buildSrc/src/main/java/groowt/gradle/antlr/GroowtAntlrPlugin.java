package groowt.gradle.antlr;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;

import java.io.File;

public final class GroowtAntlrPlugin implements Plugin<Project> {

    public static final String GROOWT_ANTLR = "groowtAntlr";

    @Override
    public void apply(Project project) {
        // register configuration
        final var configurationProvider = project.getConfigurations().register(GROOWT_ANTLR, configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
        });

        // register extension
        final var extension = project.getExtensions().create(GROOWT_ANTLR, GroowtAntlrSimpleExtension.class);

        // register tasks for each source file
        project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().forEach(sourceSet -> {
            // register sourceDirectorySet
            final var antlrSourceDirectorySet = project.getObjects().sourceDirectorySet(GROOWT_ANTLR, GROOWT_ANTLR);
            antlrSourceDirectorySet.srcDir(
                    String.join(File.separator, "src", sourceSet.getName(), "antlr")
            );
            sourceSet.getAllSource().source(antlrSourceDirectorySet);

            final var baseOutputDir = project.getLayout().getBuildDirectory().dir(String.join(
                    File.separator,
                    "generated-src",
                    "antlr",
                    sourceSet.getName()
            ));

            sourceSet.getJava().srcDir(baseOutputDir);

            final var sourceSetTasks = antlrSourceDirectorySet.getFiles().stream()
                    .filter(GroowtAntlrUtil::isAntlrFile)
                    .map(file -> {
                        final var taskProvider = project.getTasks().register(
                                GroowtAntlrUtil.getGenerateTaskName(sourceSet, file),
                                GroowtAntlrExecTask.class,
                                configurationProvider
                        );
                        taskProvider.configure(task -> {
                            task.setGroup(GROOWT_ANTLR);
                            task.getAntlrSourceFile().set(file);
                            task.getOutputDirectory().convention(
                                    baseOutputDir.flatMap(base -> base.dir(
                                            task.getPackageName().map(packageName ->
                                                    packageName.replace(".", File.separator)
                                            )
                                    ))
                            );
                            task.getVisitor().convention(extension.getVisitor());
                            task.getPackageName().convention(extension.getPackageName());
                        });
                        return taskProvider;
                    })
                    .toList();

            if (!sourceSetTasks.isEmpty()) {
                project.getTasks().register(sourceSet.getTaskName("generate", "AllAntlr"), task -> {
                    task.dependsOn(sourceSetTasks);
                    task.setGroup(GROOWT_ANTLR);
                });
            }
        });
    }

}
