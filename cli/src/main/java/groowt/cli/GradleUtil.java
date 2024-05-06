package groowt.cli;

import groowt.gradle.model.GroowtGradleModel;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.util.function.Consumer;

public final class GradleUtil {

    public static void doWith(File projectDir, Consumer<? super ProjectConnection> action) {
        final var gradleConnector = GradleConnector.newConnector().forProjectDirectory(projectDir);
        try (final var projectConnection = gradleConnector.connect()) {
            action.accept(projectConnection);
        }
    }

    public static <T> void doWith(File projectDir, Class<? extends T> modelClass, Consumer<? super T> modelConsumer) {
        doWith(projectDir, projectConnection -> {
            final T model = projectConnection.getModel(modelClass);
            modelConsumer.accept(model);
        });
    }

    public static void doWithGroowtGradleModel(File projectDir, Consumer<? super GroowtGradleModel> modelConsumer) {
        doWith(projectDir, GroowtGradleModel.class, modelConsumer);
    }

    private GradleUtil() {}

}
