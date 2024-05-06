package groowt.gradle;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class DefaultGroowtExtension implements GroowtExtension {

    private final Property<String> basePackage;

    @Inject
    public DefaultGroowtExtension(ObjectFactory objectFactory) {
        this.basePackage = objectFactory.property(String.class);
    }

    @Override
    public Property<String> getBasePackage() {
        return this.basePackage;
    }

}
