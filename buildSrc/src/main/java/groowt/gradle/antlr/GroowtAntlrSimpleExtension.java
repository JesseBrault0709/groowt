package groowt.gradle.antlr;

import org.gradle.api.provider.Property;

public interface GroowtAntlrSimpleExtension {
    Property<String> getPackageName();
    Property<Boolean> getVisitor();
}
