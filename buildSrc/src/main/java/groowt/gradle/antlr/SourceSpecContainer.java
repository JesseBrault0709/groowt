package groowt.gradle.antlr;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.antlr.AntlrSourceDirectorySet;
import org.gradle.api.tasks.SourceSet;

import javax.inject.Inject;
import java.io.File;

import static groowt.gradle.antlr.GroowtAntlrUtil.getSourceIdentifier;
import static groowt.gradle.antlr.GroowtAntlrUtil.resolveSource;

public class SourceSpecContainer extends DefaultDomainObjectSet<SourceSpec> {

    private final Project project;
    private final ObjectFactory objectFactory;
    private final Action<SourceSpec> applyConventions;

    @Inject
    public SourceSpecContainer(Project project, ObjectFactory objectFactory, Action<SourceSpec> applyConventions) {
        super(SourceSpec.class, CollectionCallbackActionDecorator.NOOP);
        this.project = project;
        this.objectFactory = objectFactory;
        this.applyConventions = applyConventions;
    }

    /**
     * @param sourceFilePaths instances of File or String
     */
    public void ignore(SourceSet sourceSet, Object... sourceFilePaths) {
        for (final Object sourceFilePath : sourceFilePaths) {
            switch (sourceFilePath) {
                case File f -> this.ignoreFile(sourceSet, f);
                case String s -> this.ignoreFile(sourceSet, new File(s));
                default -> throw new IllegalArgumentException("Can only ignore Files or Strings, given: " + sourceFilePath);
            }
        }
    }

    private void ignoreFile(SourceSet sourceSet, File target) {
        this.removeIf(potentialSourceSpec -> {
            final SourceSet potentialSourceSet = potentialSourceSpec.getResolvedSource().getSourceSet();
            if (!sourceSet.equals(potentialSourceSet)) {
                return false;
            }
            final var antlrSourceDirectorySet = sourceSet.getExtensions().getByType(AntlrSourceDirectorySet.class);
            final ResolvedSource toIgnore = resolveSource(this.project, sourceSet, antlrSourceDirectorySet, target);
            final File potentialSourceFile = potentialSourceSpec.getResolvedSource().getSourceFile();
            return toIgnore.getSourceFile().equals(potentialSourceFile);
        });
    }

    public void register(SourceSet sourceSet, String sourceFilePath) {
        this.register(sourceSet, new File(sourceFilePath), sourceSpec -> {}); // no-op action
    }

    public void register(SourceSet sourceSet, File sourceFile) {
        this.register(sourceSet, sourceFile, sourceSpec -> {});
    }

    public void register(SourceSet sourceSet, String sourceFilePath, Action<? super SourceSpec> action) {
        this.register(sourceSet, new File(sourceFilePath), action);
    }

    public void register(SourceSet sourceSet, File sourceFile, Action<? super SourceSpec> action) {
        final var antlrSourceDirectorySet = sourceSet.getExtensions().getByType(AntlrSourceDirectorySet.class);
        final var resolvedSource = resolveSource(this.project, sourceSet, antlrSourceDirectorySet, sourceFile);

        final String identifier = getSourceIdentifier(resolvedSource);
        final var specOptional = this.stream().filter(sourceSpec -> sourceSpec.getIdentifier().equals(identifier)).findFirst();

        if (specOptional.isPresent()) {
            // we already have one, so find and run the action against it
            final var spec = specOptional.get();
            action.execute(spec);
        } else {
            // create a new one
            final var spec = this.objectFactory.newInstance(SourceSpec.class, identifier, resolvedSource);
            this.applyConventions.execute(spec);
            action.execute(spec);
            this.add(spec);
        }
    }

}
