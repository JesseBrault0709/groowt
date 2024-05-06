package groowt.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.GroovyPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSetContainer;

import static org.gradle.api.internal.lambdas.SerializableLambdas.spec;

public class GroowtGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // Apply java and groovy plugins, if not done already
        final var pluginManager = project.getPluginManager();
        pluginManager.apply(JavaPlugin.class);
        pluginManager.apply(GroovyPlugin.class);

        // Create our groowt configuration for storing the groowt dependencies
        final Provider<Configuration> groowtConfigurationProvider = project.getConfigurations()
                .register("groowt", configuration -> {
                    configuration.setCanBeConsumed(false);
                    configuration.setCanBeResolved(true);
                });

        // Create groowt extension and source sets.
        final GroowtExtension groowtExtension = project.getExtensions().create(
                GroowtExtension.class,
                "groowt",
                DefaultGroowtExtension.class
        );

        final JavaPluginExtension javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        final SourceSetContainer javaSourceSets = javaExtension.getSourceSets();

        // data resources, such as texts, json files, sqlite-databases, etc.
        javaSourceSets.getByName("main", mainSourceSet -> {
            mainSourceSet.getResources().srcDir("src/data");
        });

        // TODO: figure out how we can set the compile behavior for all of these.
        javaSourceSets.forEach(sourceSet -> {
            final TemplateSourceSet templateSourceSet = project.getObjects().newInstance(
                    DefaultTemplateSourceSet.class,
                    "wvc",
                    ((DefaultSourceSet) sourceSet).getDisplayName()
            );
            final TemplateSourceDirectorySet templateSourceDirectorySet = templateSourceSet.getTemplates();
            sourceSet.getExtensions().add(
                    TemplateSourceDirectorySet.class,
                    "templates",
                    templateSourceDirectorySet
            );
            templateSourceDirectorySet.srcDir("src/" + sourceSet.getName() + "/templates");

            // Explicitly capture only a FileCollection in the lambda below for compatibility with configuration-cache.
            @SuppressWarnings("UnnecessaryLocalVariable")
            final FileCollection templateSourceFiles = templateSourceDirectorySet;
            sourceSet.getResources().getFilter().exclude(
                    spec(element -> templateSourceFiles.contains(element.getFile()))
            );
            sourceSet.getAllJava().source(templateSourceDirectorySet);
            sourceSet.getAllSource().source(templateSourceDirectorySet);
        });

        // create init task
        project.getTasks().create("groowtInit", GroowtInitTask.class, groowtConfigurationProvider);
    }

}
