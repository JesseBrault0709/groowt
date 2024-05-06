package groowt.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.util.Set;

public class GroowtInitTask extends DefaultTask {

    private static final Logger logger = LoggerFactory.getLogger(GroowtInitTask.class);

    private static final Set<String> srcDirsToMake = Set.of("groovy", "templates");

    private final Provider<Configuration> groowtConfigurationProvider;
    private final File binDir;
    private final File groowtDir;

    @Inject
    public GroowtInitTask(Project project, Provider<Configuration> groowtConfigurationProvider) {
        this.groowtConfigurationProvider = groowtConfigurationProvider;
        this.binDir = new File(project.getRootDir(), "bin");
        this.groowtDir = new File(project.getRootDir(), "groowt");
    }

    protected void createBin() throws IOException {
        //noinspection ResultOfMethodCallIgnored
        this.binDir.mkdirs();
        final var groowtFile = new File(this.binDir, "groowt");
        try (final InputStream bootstrapInputStream = this.getClass().getResourceAsStream("groowt")) {
            if (bootstrapInputStream != null) {
                try (final OutputStream bootstrapOutputStream = new FileOutputStream(groowtFile)) {
                    bootstrapInputStream.transferTo(bootstrapOutputStream);
                }
                if (!groowtFile.setExecutable(true)) {
                    logger.warn("Could not set bin/groowt to executable; you will have to do this yourself.");
                }
            } else {
                throw new RuntimeException("Could not find groowt shell script.");
            }
        }
    }

    protected void createGroowtFolder() throws IOException {
        //noinspection ResultOfMethodCallIgnored
        this.groowtDir.mkdirs();
        final var groowtConfiguration = this.groowtConfigurationProvider.get();
        final Set<File> groowtCliFiles = groowtConfiguration.files(dependency -> {
            final var group = dependency.getGroup();
            if (group == null || !group.equals("groowt")) {
                return false;
            } else {
                return dependency.getName().equals("groowt-cli");
            }
        });
        final File groowtCliJarFile = groowtCliFiles.stream()
                .filter(file -> file.getName().endsWith(".jar"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find groowt-cli jar file."));
        final File groowtCliJarOutputFile = new File(this.groowtDir, "groowt-cli.jar");
        try (final InputStream groowtCliJarInputStream = new FileInputStream(groowtCliJarFile)) {
            try (final OutputStream groowtCliJarOutputStream = new FileOutputStream(groowtCliJarOutputFile)) {
                groowtCliJarInputStream.transferTo(groowtCliJarOutputStream);
            }
        }
    }

    protected void createSrcDirs() {
        final var javaPluginExtension = this.getProject().getExtensions().getByType(JavaPluginExtension.class);
        javaPluginExtension.getSourceSets().forEach(sourceSet -> {
            final var srcDirs = sourceSet.getAllSource().getSrcDirs();
            srcDirs.forEach(srcDir -> {
                if (!sourceSet.getName().contains("test") && srcDirsToMake.contains(srcDir.getName())) {
                    //noinspection ResultOfMethodCallIgnored
                    srcDir.mkdirs();
                }
            });
        });
    }

    @TaskAction
    public void doInit() throws IOException {
        this.createBin();
        this.createGroowtFolder();
        this.createSrcDirs();
    }

}
