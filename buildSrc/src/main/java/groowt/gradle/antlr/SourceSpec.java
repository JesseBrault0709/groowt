package groowt.gradle.antlr;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

import javax.inject.Inject;

public abstract class SourceSpec {

    private final String identifier;
    private final ResolvedSource resolvedSource;

    @Inject
    public SourceSpec(String identifier, ResolvedSource resolvedSource) {
        this.identifier = identifier;
        this.resolvedSource = resolvedSource;
    }

    @Internal
    public String getIdentifier() {
        return this.identifier;
    }

    @Nested
    public ResolvedSource getResolvedSource() {
        return this.resolvedSource;
    }

    @Input
    public abstract Property<Boolean> getIsCompileDependency();

    @Input
    public abstract Property<Boolean> getDebug();

    @Input
    public abstract Property<String> getPackageName();

    @Input
    public abstract Property<Boolean> getVisitor();

}
