package groowt.view.component.web.tools

import picocli.CommandLine

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.function.Function

abstract class SourceFileProcessorSpec implements Callable<Integer> {

    @CommandLine.Parameters(
            arity = '1..*',
            defaultValue = '*.wvc',
            description = 'The web view component source file(s) to process, as globs relative to the source directory.'
    )
    List<String> targets

    @CommandLine.Option(
            names = ['--dryRun'],
            defaultValue = 'false',
            description = 'Do not actually write the tree files to disk.'
    )
    boolean dryRun

    @CommandLine.Option(
            names = ['-s', '--suffix'],
            description = 'The suffix to append to the written tree file name(s).'
    )
    Optional<String> suffix

    @CommandLine.Option(
            names = ['-e', '--extension'],
            defaultValue = '.txt',
            description = 'The extension to append to the written tree file(s).'
    )
    String extension

    @CommandLine.Option(
            names = ['-d', '--sourceDirectory'],
            description = 'The directory where the source files are stored.'
    )
    Optional<String> srcDir

    @CommandLine.Option(
            names = ['-o', '--outputDirectory'],
            description = 'The directory to which the output files should be written.'
    )
    Optional<File> outputDirectory

    @CommandLine.Option(
            names = ['-y', '--yes'],
            description = 'Whether to automatically write all formatted trees to disk, including cases where old data will be written over.'
    )
    boolean autoYes

    @CommandLine.Option(
            names = ['-v', '--verbose'],
            description = 'Whether to output verbose information during processing.'
    )
    boolean verbose

    private final Function<SourceFileProcessorSpec, SourceFileProcessor> getSourceFileProcessor
    private final String defaultSourceDir

    SourceFileProcessorSpec(
            Function<SourceFileProcessorSpec, SourceFileProcessor> getSourceFileProcessor,
            String defaultSourceDir
    ) {
        this.getSourceFileProcessor = getSourceFileProcessor
        this.defaultSourceDir = defaultSourceDir
    }

    @Override
    Integer call() throws Exception {
        def fs = FileSystems.default
        def resolvedSrcDir = this.srcDir.orElse(this.defaultSourceDir)
        def srcDirPath = Path.of(resolvedSrcDir)
        def globBase = 'glob:' + resolvedSrcDir
        def sourceFiles = this.targets.collectMany {
            def matcher = fs.getPathMatcher(globBase + File.separator + it)
            Files.walk(srcDirPath).filter { matcher.matches(it) }
                    .map { it.toFile() }
                    .filter { it.isFile() }
                    .toList()
        }

        if (sourceFiles.isEmpty()) {
            System.err.println('There are no matching source files for the given targets.')
            return 1
        }

        def processor = this.getSourceFileProcessor.apply(this)
        sourceFiles.each(processor.&process)

        return 0
    }

}
