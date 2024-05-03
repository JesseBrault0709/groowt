//file:noinspection ConfigurationAvoidance
package groowt.gradle.antlr

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.antlr.AntlrPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow

class GroowtAntlrPluginTests {

    @Disabled('TODO: figure out why register(SourceSet, String) is not working.')
    @Test
    void smokeScreen() {
        def projectDir = File.createTempDir()
        new FileTreeBuilder(projectDir).tap {
            dir(['src', 'main', 'antlr'].join(File.separator)) {
                file('MyGrammar.g4') {
                    write("parser grammar MyGrammar;")
                }
            }
        }
        def project = ProjectBuilder.builder().with {
            withProjectDir(projectDir)
            build()
        }

        project.pluginManager.with {
            apply(JavaPlugin)
            apply(AntlrPlugin)
            apply(GroowtAntlrPlugin)
        }

        def mainSourceSet = project.extensions.getByType(JavaPluginExtension).sourceSets.findByName('main')

        project.extensions.getByType(GroowtAntlrExtension).sourceSpecs.with {
            register(mainSourceSet, 'MyGrammar.g4')
        }

        def findTask = {
            project.tasks.named('generateMyGrammar', GroowtAntlrTask)
        } as Executable

        assertDoesNotThrow(findTask) {
            "Could not find task 'generateMyGrammar' (all tasks: ${project.tasks*.name.join(', ')})"
        }
    }

}
