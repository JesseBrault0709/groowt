package groowt.gradle.model;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface GroowtGradleModel {

    String getBasePackage();

    Map<String, Set<File>> getSourceSetToTemplatesDirs();

}
