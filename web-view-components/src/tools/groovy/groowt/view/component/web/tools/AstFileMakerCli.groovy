package groowt.view.component.web.tools

import picocli.CommandLine

@CommandLine.Command(
        name = 'astFileMaker',
        mixinStandardHelpOptions = true,
        description = 'Create ast file(s) from given source file(s).'
)
class AstFileMakerCli extends SourceFileProcessorSpec {

    static void main(String[] args) {
        System.exit(new CommandLine(new AstFileMakerCli()).execute(args))
    }

    AstFileMakerCli() {
        super({ SourceFileProcessorSpec spec ->
            new AstFileMaker(
                    dryRun: spec.dryRun,
                    suffix: spec.suffix.orElse('_ast'),
                    extension: spec.extension,
                    outputDirectory: spec.outputDirectory.orElse(new File('src/test/ast/trees')),
                    autoYes: spec.autoYes,
                    verbose: spec.verbose
            )
        }, 'src/test/ast')
    }

}
