package groowt.gradle.publish

import com.jessebrault.jbarchiva.JbArchivaPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

class GroowtPublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(MavenPublishPlugin)
        project.plugins.apply(JbArchivaPlugin)
    }

}
