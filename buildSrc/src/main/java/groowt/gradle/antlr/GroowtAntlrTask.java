package groowt.gradle.antlr;

import org.gradle.api.Action;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.antlr.AntlrTask;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

public class GroowtAntlrTask extends AntlrTask {

    private final ObjectFactory objectFactory;
    private final SourceSpec sourceSpec;

    private Provider<Directory> outputDirectory;
    private NullableProviderList<String> arguments;

    @Inject
    public GroowtAntlrTask(ObjectFactory objectFactory, SourceSpec sourceSpec, Action<? super GroowtAntlrTask> configure) {
        this.objectFactory = objectFactory;
        this.sourceSpec = sourceSpec;
        configure.execute(this);
    }

    @Nested
    public SourceSpec getSourceSpec() {
        return this.sourceSpec;
    }

    @Override
    public @NotNull File getOutputDirectory() {
        return this.outputDirectory.get().getAsFile();
    }

    @Override
    public void setOutputDirectory(@NotNull File outputDirectory) {
        final DirectoryProperty directoryProperty = this.objectFactory.directoryProperty();
        directoryProperty.set(outputDirectory);
        this.outputDirectory = directoryProperty;
    }

    public void setOutputDirectory(Provider<Directory> outputDirectoryProvider) {
        this.outputDirectory = outputDirectoryProvider;
    }

    @Override
    @Internal
    public @NotNull List<String> getArguments() {
        return this.arguments.getElements();
    }

    @Override
    public void setArguments(@NotNull List<String> arguments) {
        this.arguments = new NullableProviderList<>();
        this.arguments.addAllElements(arguments);
    }

    public void setArguments(@NotNull NullableProviderList<String> arguments) {
        this.arguments = arguments;
    }

}
