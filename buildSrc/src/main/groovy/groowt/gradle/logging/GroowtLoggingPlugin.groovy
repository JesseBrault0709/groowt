package groowt.gradle.logging

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension

class GroowtLoggingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def javaExtension = project.extensions.getByType(JavaPluginExtension)

        def defaultLog4j2XmlResource = this.class.getResource('default-log4j2.xml')
        if (defaultLog4j2XmlResource == null) {
            throw new RuntimeException('default-log4j2.xml is null')
        }

        def tmpLog4j2Xml  = File.createTempFile('default-log4j2-xml', 'tmp')
        defaultLog4j2XmlResource.withReader { reader ->
            tmpLog4j2Xml.withWriter {
                reader.transferTo(it)
            }
        }

        javaExtension.sourceSets.each { sourceSet ->
            project.tasks.register(
                    sourceSet.getTaskName('copyLoggingConfigTo', 'Resources'),
                    GroowtCopyLoggerConfigTask
            ) { task ->
                task.group = 'logging'
                task.from(tmpLog4j2Xml)
                task.rename { 'log4j2.xml' }
                task.into(['src', sourceSet.name, 'resources'].join(File.separator))
            }
        }

        def libs = project.extensions.getByType(VersionCatalogsExtension).named('libs')

        project.dependencies.addProvider(
                'implementation', libs.findLibrary('slf4j-api').orElseThrow()
        )
        project.dependencies.addProvider(
                'runtimeOnly', libs.findLibrary('log4j-core').orElseThrow()
        )
        project.dependencies.addProvider(
                'runtimeOnly', libs.findLibrary('log4j-slf4jBinding').orElseThrow()
        )
    }

}
