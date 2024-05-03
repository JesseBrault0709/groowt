//file:noinspection ConfigurationAvoidance
package groowt.gradle.antlr

import groovy.transform.TupleConstructor
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.antlr.AntlrPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import java.nio.file.Path

import static groowt.gradle.antlr.GroowtAntlrUtil.*
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertIterableEquals
import static org.junit.jupiter.api.DynamicTest.dynamicTest

class GroowtAntlrUtilTests {

    @TupleConstructor
    private static class SourceFileToIdentifierTestSpec {
        File srcDir
        File srcFile
        List<String> expected
    }

    @TestFactory
    Collection<DynamicTest> sourceFileToIdentifierPartsTests() {
        def srcDir = File.createTempDir()
        def getSpec = { String path, List<String> expected ->
            new SourceFileToIdentifierTestSpec(srcDir, new File(path), expected)
        }

        def specs = [
                getSpec('MyGrammar.g4', ['MyGrammar']),
                getSpec('subDir/MyGrammar.g4', ['subDir', 'MyGrammar']),
                getSpec('subDir/subSubDir/MyGrammar.g4', ['subDir', 'subSubDir', 'MyGrammar']),
                getSpec('My.grammar.g4', ['My.grammar'])
        ]

        return specs.collect { spec ->
            dynamicTest(spec.srcFile.toString()) {
                def actual = sourceFileToIdentifierParts(spec.srcDir, spec.srcFile)
                assertIterableEquals(spec.expected, actual) {
                    "Unexpected result: ${actual}"
                }
            }
        }
    }

    @TestFactory
    Collection<DynamicTest> getSourceIdentifierTests() {
        def projectDir = File.createTempDir()
        File srcDir
        new FileTreeBuilder(projectDir).with {
            srcDir = dir(['src', 'main', 'antlr'].join(File.separator))
        }

        def project = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .build()
        project.pluginManager.with {
            apply(JavaPlugin)
            apply(AntlrPlugin)
        }
        def mainSourceSet = project.extensions.getByType(JavaPluginExtension)
                .sourceSets.getByName('main')

        def getResolvedSource = { String path ->
            new ResolvedSource(project, mainSourceSet, srcDir, new File(path))
        }

        Closure<Tuple2<String, ResolvedSource>> getSpec = { String expected, String path ->
            new Tuple2(expected, getResolvedSource(path))
        }

        def specs = [
                getSpec('MyGrammar', 'MyGrammar.g4'),
                getSpec('SubDirMyGrammar', 'subDir/MyGrammar.g4')
        ]

        return specs.collect { spec ->
            dynamicTest(spec.v2.sourceFile.toString()) {
                def actual = getSourceIdentifier(spec.v2)
                assertEquals(spec.v1, actual)
            }
        }
    }

    @TestFactory
    Collection<DynamicTest> getOutputDirTests() {
        def project = ProjectBuilder.builder().build()
        project.pluginManager.tap {
            apply(JavaPlugin)
            apply(AntlrPlugin)
        }
        project.layout.buildDirectory.set(new File('build'))

        def mainSourceSet = project.extensions.getByType(JavaPluginExtension)
                .sourceSets.getByName('main')

        Closure<Tuple3<Path, SourceSet, Provider<String>>> getSpec = { String givenPackageName ->
            def expectedPackagePath = givenPackageName.replace('.', File.separator)
            def expected = Path.of('build', ['generated-src', 'antlr', mainSourceSet.name, expectedPackagePath] as String[])
            def packageProperty = project.objects.property(String).tap { set(givenPackageName) }
            new Tuple3(expected, mainSourceSet, packageProperty)
        }

        List<Tuple3<Path, SourceSet, Provider<String>>> specs = [
                getSpec('antlr.one.two.three'), // build/generated-src/antlr/main/antlr/one/two/three
                getSpec('test.antlr'), // build/generated-src/antlr/main/test/antlr
                getSpec('antlr'), // build/generated-src/antlr/main/antlr
                getSpec('') // build/generated-src/antlr/main
        ]

        def projectPath = project.layout.projectDirectory.asFile.toPath()

        return specs.collect { spec ->
            def givenPackageName = spec.v3.get()
            dynamicTest(givenPackageName.empty ? '<empty>' : givenPackageName) {
                def actualPath = getOutputDirectory(project, spec.v2, spec.v3)
                        .get().asFile.toPath()
                def result = projectPath.relativize(actualPath)
                assertEquals(spec.v1, result)
            }
        }
    }

}
