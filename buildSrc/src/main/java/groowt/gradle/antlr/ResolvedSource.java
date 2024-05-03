package groowt.gradle.antlr;

import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.SourceSet;

import java.io.File;
import java.util.List;

import static groowt.gradle.antlr.GroowtAntlrUtil.relativize;

public final class ResolvedSource {

    private final Project project;
    private final SourceSet sourceSet;
    private final File sourceDir;
    private final File sourceFile;

    public ResolvedSource(Project project, SourceSet sourceSet, File sourceDir, File sourceFile) {
        this.project = project;
        this.sourceSet = sourceSet;
        this.sourceDir = sourceDir;
        this.sourceFile = sourceFile;
    }

    @Internal
    public Project getProject() {
        return this.project;
    }

    @Internal
    public SourceSet getSourceSet() {
        return this.sourceSet;
    }

    @InputDirectory
    public File getSourceDir() {
        return this.sourceDir;
    }

    @InputFile
    public File getSourceFile() {
        return this.sourceFile;
    }

    private String relativizeSourceDir() {
        return relativize(this.project.getProjectDir(), this.sourceDir).toString();
    }

    private String relativeSourceFile() {
        return relativize(this.sourceDir, this.sourceFile).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ResolvedSource other) {
            return this.project.equals(other.project)
                    && this.sourceSet.equals(other.sourceSet)
                    && this.sourceDir.equals(other.sourceDir)
                    && this.sourceFile.equals(other.sourceFile);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.project.hashCode();
        result = 31 * result + this.sourceSet.hashCode();
        result = 31 * result + this.sourceDir.hashCode();
        result = 31 * result + this.sourceFile.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ResolvedSource("
                + String.join(", ", List.of(
                        "sourceSet: " + this.sourceSet.getName(),
                        "sourceDir: " + this.relativizeSourceDir(),
                        "sourceFile: " + this.relativeSourceFile()
                ))
                + ")";
    }

}
