package groowt.gradle;

import org.gradle.api.provider.Property;

public interface GroowtExtension {
    Property<String> getBasePackage();
}
