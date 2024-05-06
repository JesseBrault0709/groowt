package groowt.cli;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.util.function.Consumer;

public final class GradleUtil {

    public static void doWith(File projectDir, Consumer<ProjectConnection> action) {
        final var gradleConnector = GradleConnector.newConnector().forProjectDirectory(projectDir);
        try (final var projectConnection = gradleConnector.connect()) {
            action.accept(projectConnection);
        }
    }

    private GradleUtil() {}

}
