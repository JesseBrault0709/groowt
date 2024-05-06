package groowt.gradle;

import org.gradle.api.internal.tasks.DefaultSourceSetContainer;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class DefaultGroowtExtension implements GroowtExtension {

    private final SourceSetContainer sourceSets;

    @Inject
    public DefaultGroowtExtension(ObjectFactory objectFactory) {
        this.sourceSets = objectFactory.newInstance(DefaultSourceSetContainer.class);
    }

    @Override
    public SourceSetContainer getSourceSets() {
        return this.sourceSets;
    }

}
