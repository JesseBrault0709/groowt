package groowt.gradle.model;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DefaultGroowtGradleModel implements GroowtGradleModel, Serializable {

    private String basePackage;
    private Map<String, Set<File>> sourceSetToTemplatesDirs;

    @Override
    public String getBasePackage() {
        return this.basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = Objects.requireNonNull(basePackage);
    }

    @Override
    public Map<String, Set<File>> getSourceSetToTemplatesDirs() {
        return Objects.requireNonNull(this.sourceSetToTemplatesDirs);
    }

    public void setSourceFileSets(Map<String, Set<File>> sourceSetToTemplateDir) {
        this.sourceSetToTemplatesDirs = sourceSetToTemplateDir;
    }



}
