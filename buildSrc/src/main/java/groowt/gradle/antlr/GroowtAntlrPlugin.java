package groowt.gradle.antlr;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.antlr.AntlrPlugin;
import org.gradle.api.plugins.antlr.AntlrSourceDirectorySet;
import org.gradle.api.plugins.antlr.AntlrTask;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static groowt.gradle.antlr.GroowtAntlrUtil.*;

public final class GroowtAntlrPlugin implements Plugin<Project> {

    public static final String taskGroup = "groowtAntlr";

    private static final String packageArg = "-package";
    private static final String traceArg = "-trace";
    private static final String visitorArg = "-visitor";

    private final ObjectFactory objectFactory;

    @Inject
    public GroowtAntlrPlugin(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    private static NullableProviderList<String> getArguments(SourceSpec sourceSpec) {
        final NullableProviderList<String> arguments = new NullableProviderList<>();
        arguments.addCollectionProvider(sourceSpec.getPackageName().map(packageName -> List.of(packageArg, packageName)));
        arguments.addProvider(sourceSpec.getVisitor().map(isVisitor -> isVisitor ? visitorArg : null));
        arguments.addProvider(sourceSpec.getDebug().map(isDebug -> isDebug ? traceArg : null));
        return arguments;
    }

    // For afterEvaluate!
    private static List<GroowtAntlrTask> createAndRegister(Project project, GroowtAntlrExtension extension) {
        final TaskContainer taskContainer = project.getTasks();
        return extension.getSourceSpecs().stream().map(sourceSpec -> {
            return taskContainer.create(
                    getGenerateTaskName(sourceSpec),
                    GroowtAntlrTask.class,
                    sourceSpec,
                    (Action<GroowtAntlrTask>) task -> {
                        task.setGroup(taskGroup);
                        task.setArguments(getArguments(sourceSpec));
                        task.setSource(sourceSpec.getResolvedSource().getSourceFile());
                        task.setOutputDirectory(
                                getOutputDirectory(
                                        project,
                                        sourceSpec.getResolvedSource().getSourceSet(),
                                        sourceSpec.getPackageName()
                                )
                        );
                    }
            );
        }).toList();
    }

    // For afterEvaluate!
    private static void addCompileDependencies(Project project, List<GroowtAntlrTask> tasks) {
        tasks.forEach(task -> {
            // if it is a compile dependency, add it as input to java source set
            final var isCompileDependency = task.getSourceSpec().getIsCompileDependency().get();
            if (isCompileDependency) {
                project.getTasks().withType(JavaCompile.class).configureEach(javaCompile -> {
                    javaCompile.dependsOn(task);
                });
            }
        });
    }

    private record GenerateAllSpec(
            @Nullable FileTree source,
            @NotNull List<String> args,
            @NotNull Provider<String> packageName
    ) {}

    private GenerateAllSpec getBlankGenerateAllSpec() {
        return new GenerateAllSpec(null, List.of(), this.objectFactory.property(String.class));
    }

    private static GenerateAllSpec getGenerateAllSpecFromTask(GroowtAntlrTask task) {
        return new GenerateAllSpec(task.getSource(), task.getArguments(), task.getSourceSpec().getPackageName());
    }

    private static @Nullable FileTree combineFileTrees(@Nullable FileTree f0, @Nullable FileTree f1) {
        if (f0 != null && f1 != null) {
            return f0.plus(f1);
        } else if (f0 != null) {
            return f0;
        } else {
            return f1; // null
        }
    }

    private static List<String> combineArguments(List<String> a0, List<String> a1) {
        final List<String> result = new ArrayList<>(a0);
        final Iterator<String> a1Iter = a1.iterator();
        while (a1Iter.hasNext()) {
            final String arg = a1Iter.next();
            if (arg.equals(packageArg) && result.contains(packageArg)) {
                if (!a1Iter.hasNext()) {
                    throw new IllegalStateException("shouldn't get here");
                }
                final String a0PackageName = result.get(result.indexOf(arg) + 1);
                final String a1PackageName = a1Iter.next();
                if (!a0PackageName.equals(a1PackageName)) {
                    throw new IllegalArgumentException("Cannot have separate package arguments for two files from the same source set.");
                }
            } else if (!result.contains(arg)) {
                result.add(arg);
            }
        }
        return result;
    }

    private static Provider<String> combinePackageNames(Provider<String> p0, Provider<String> p1) {
        return p0.zip(p1, (pn0, pn1) -> {
            if (!pn0.equals(pn1)) {
                throw new IllegalArgumentException("Cannot have separate package names for two files from the same source set.");
            }
            return pn0;
        });
    }

    private static GenerateAllSpec combineGenerateAllSpecs(GenerateAllSpec s0, GenerateAllSpec s1) {
        return new GenerateAllSpec(
                combineFileTrees(s0.source(), s1.source()),
                combineArguments(s0.args(), s1.args()),
                combinePackageNames(s0.packageName(), s1.packageName())
        );
    }

    // For afterEvaluate!
    private static void addGenerateAllTasks(Project project, List<GroowtAntlrTask> tasks) {
        final Map<SourceSet, List<GroowtAntlrTask>> sourceSetToTasks = tasks.stream().collect(Collectors.groupingBy(
                task -> task.getSourceSpec().getResolvedSource().getSourceSet()
        ));

        final Map<SourceSet, GenerateAllSpec> sourceSetToSpec = new HashMap<>();
        sourceSetToTasks.forEach((sourceSet, sourceSetTasks) -> {
            List<GenerateAllSpec> specs = sourceSetTasks.stream().map(task ->
                new GenerateAllSpec(task.getSource(), task.getArguments(), task.getSourceSpec().getPackageName())
            ).toList();
            specs.stream().reduce(GroowtAntlrPlugin::combineGenerateAllSpecs).ifPresent(allSpec -> {
                sourceSetToSpec.put(sourceSet, allSpec);
            });
        });

        sourceSetToSpec.forEach((sourceSet, spec) -> {
            project.getTasks().register(
                    getGenerateAllTaskName(sourceSet),
                    GroowtAntlrAllTask.class,
                    task -> {
                        task.setGroup(taskGroup);
                        if (spec.source() != null) {
                            task.setSource(spec.source());
                        }
                        task.setArguments(spec.args());
                        task.setOutputDirectory(
                                getOutputDirectory(project, sourceSet, spec.packageName()).get().getAsFile()
                        );
                    }
            );
        });
    }

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(AntlrPlugin.class);

        // undo the antlr plugin creating its own tasks
        project.getTasks().withType(AntlrTask.class, antlrTask -> {
            if (!(antlrTask instanceof GroowtAntlrTask || antlrTask instanceof GroowtAntlrAllTask)) {
                antlrTask.setEnabled(false);
            }
        });

        // create extension
        final GroowtAntlrExtension extension = project.getExtensions().create("groowtAntlr", GroowtAntlrExtension.class);
        extension.getPackageName().convention("");
        extension.getVisitor().convention(false);

        // find all antlr files first and add them to extension
        project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().forEach(sourceSet -> {
            final Set<File> antlrFiles = sourceSet.getExtensions().getByType(AntlrSourceDirectorySet.class).getFiles();
            for (final File antlrFile : antlrFiles) {
                if (isAntlrSourceFile(antlrFile)) {
                    extension.getSourceSpecs().register(sourceSet, antlrFile);
                }
            }
        });

        // after evaluate, generate tasks for each registered sourceSpec
        project.afterEvaluate(postEvaluateProject -> {
            final List<GroowtAntlrTask> tasks = createAndRegister(postEvaluateProject, extension);
            addCompileDependencies(project, tasks);
            addGenerateAllTasks(project, tasks);
        });
    }

}
