package groowt.gradle.antlr;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public abstract class GroowtAntlrExtension {

    private final SourceSpecContainer sourceSpecs;

    @Inject
    public GroowtAntlrExtension(ObjectFactory objectFactory) {
        this.sourceSpecs = objectFactory.newInstance(SourceSpecContainer.class, (Action<SourceSpec>) sourceSpec -> {
            sourceSpec.getPackageName().convention(this.getPackageName());
            sourceSpec.getVisitor().convention(this.getVisitor());
            sourceSpec.getIsCompileDependency().convention(true);
            sourceSpec.getDebug().convention(false);
        });
    }

    public abstract Property<String> getPackageName();
    public abstract Property<Boolean> getVisitor();

    public SourceSpecContainer getSourceSpecs() {
        return this.sourceSpecs;
    }

    public void sourceSpecs(Action<SourceSpecContainer> configure) {
        configure.execute(this.getSourceSpecs());
    }

}
