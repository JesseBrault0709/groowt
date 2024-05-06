package groowt.cli;

import groowt.gradle.model.GroowtGradleModel;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

@Command(name = "generate", aliases = "gen", description = "Generate a component, template, model, etc.")
public class Generate implements Callable<Integer> {

    @CommandLine.ParentCommand
    private GroowtCli cli;

    @CommandLine.Option(
            names = { "-c", "--component" },
            description = "Create a component with the given name."
    )
    private String componentName;

    @CommandLine.Option(
            names = { "-s", "--sourceSet" },
            description = "The source set in which to generate the component, etc.",
            defaultValue = "main"
    )
    private String sourceSet;

    @CommandLine.Option(
            names = { "-d", "--srcDir", "--sourceDir", "--sourceDirectory" },
            description = "The directory in the source set in which to generate the component, etc."
    )
    private File sourceDir;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public Integer call() {
        if (this.componentName != null) {
            GradleUtil.doWith(this.cli.getProjectDir(), project -> {
                final var model = project.getModel(GroowtGradleModel.class);
                if (sourceDir == null) {
                    this.sourceDir = new File(String.join(File.separator, "src", this.sourceSet, "groovy"));
                }
                final File packageDir = new File(
                        this.sourceDir, model.getBasePackage().replace(".", File.separator)
                );
                packageDir.mkdirs();
                final File componentFile = new File(packageDir, this.componentName + ".txt");
                try (final OutputStream componentFileOutputStream = new FileOutputStream(componentFile)) {
                    componentFileOutputStream.write("Hello, Groowt!".getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return 0;
    }

}
