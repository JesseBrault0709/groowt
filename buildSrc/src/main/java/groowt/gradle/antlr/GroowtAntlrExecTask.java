package groowt.gradle.antlr;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;

import javax.inject.Inject;
import java.io.File;

public class GroowtAntlrExecTask extends DefaultTask {

    private final ExecOperations execOperations;
    private final Provider<Configuration> configurationProvider;
    private final RegularFileProperty antlrSourceFile;
    private final DirectoryProperty outputDirectory;
    private final Property<String> packageName;
    private final Property<Boolean> visitor;

    @Inject
    public GroowtAntlrExecTask(
            ObjectFactory objectFactory,
            ExecOperations execOperations,
            Provider<Configuration> configurationProvider
    ) {
        this.execOperations = execOperations;
        this.configurationProvider = configurationProvider;
        this.antlrSourceFile = objectFactory.fileProperty();
        this.outputDirectory = objectFactory.directoryProperty();
        this.packageName = objectFactory.property(String.class);
        this.visitor = objectFactory.property(Boolean.class);
        this.visitor.convention(false);
    }

    @InputFile
    public RegularFileProperty getAntlrSourceFile() {
        return this.antlrSourceFile;
    }

    @OutputDirectory
    public DirectoryProperty getOutputDirectory() {
        return this.outputDirectory;
    }

    @Option(option = "packageName", description = "The packageName argument for Antlr.")
    @Input
    public Property<String> getPackageName() {
        return this.packageName;
    }

    @Option(option = "visitor", description = "Whether Antlr should generate visitor classes or not.")
    @Input
    public Property<Boolean> getVisitor() {
        return this.visitor;
    }

    public File resolveOutputFile(String name) {
        return new File(this.outputDirectory.get().getAsFile(), name);
    }

    @TaskAction
    public void doGenerate() {
        final Configuration antlrConfiguration = configurationProvider.get();

        final ExecResult result = this.execOperations.javaexec(javaExecSpec -> {
            javaExecSpec.classpath(antlrConfiguration);
            javaExecSpec.getMainClass().set("org.antlr.v4.Tool");

            final String visitorArg = this.visitor.map(isVisitor -> isVisitor ? "-visitor" : "").get();
            if (!visitorArg.isEmpty()) {
                javaExecSpec.args(visitorArg);
            }
            final String packageNameArg = this.packageName.getOrElse("");
            if (!packageNameArg.isEmpty()) {
                javaExecSpec.args("-package", packageNameArg);
            }
            javaExecSpec.args("-o", this.outputDirectory.get().getAsFile().toString());

            javaExecSpec.args(this.antlrSourceFile.getAsFile().get().toString());
        });

        result.assertNormalExitValue();
    }

}
