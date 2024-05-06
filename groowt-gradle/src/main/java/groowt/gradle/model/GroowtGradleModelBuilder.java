package groowt.gradle.model;

import groowt.gradle.GroowtExtension;
import groowt.gradle.TemplateSourceDirectorySet;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroowtGradleModelBuilder implements ToolingModelBuilder {

    @Override
    public boolean canBuild(String modelName) {
        return modelName.equals(GroowtGradleModel.class.getName());
    }

    @Override
    public @NotNull Object buildAll(@NotNull String modelName, Project project) {
        final DefaultGroowtGradleModel model = new DefaultGroowtGradleModel();
        final var groowtExtension = project.getExtensions().getByType(GroowtExtension.class);

        // base package
        final Property<String> basePackage = groowtExtension.getBasePackage();
        if (!basePackage.isPresent()) {
            throw new RuntimeException(
                    "The property 'basePackage' must be set under the 'groowt' extension in build.gradle"
            );
        }
        model.setBasePackage(basePackage.get());

        // templates dirs
        final Map<String, Set<File>> sourceSetToTemplatesDirs = new HashMap<>();
        final var javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        javaExtension.getSourceSets().forEach(sourceSet -> {
            final TemplateSourceDirectorySet templateSourceDirectorySet =
                    sourceSet.getExtensions().getByType(TemplateSourceDirectorySet.class);
            sourceSetToTemplatesDirs.put(sourceSet.getName(), templateSourceDirectorySet.getFiles());
        });
        model.setSourceFileSets(sourceSetToTemplatesDirs);

        return model;
    }

}
