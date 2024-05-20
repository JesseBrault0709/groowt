package groowt.view.web.tools

import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
        name = 'parseTreeFileMaker',
        mixinStandardHelpOptions = true,
        description = 'Create parse tree file(s) from given source file(s).'
)
class ParseTreeFileMakerCli extends SourceFileProcessorSpec {

    static void main(String[] args) {
        System.exit(new CommandLine(new ParseTreeFileMakerCli()).execute(args))
    }

    ParseTreeFileMakerCli() {
        super({ SourceFileProcessorSpec spec ->
            new ParseTreeFileMaker(
                    dryRun: spec.dryRun,
                    suffix: spec.suffix.orElse('_parseTree'),
                    extension: spec.extension,
                    outputDirectory: spec.outputDirectory.orElse(new File('src/test/parser/trees')),
                    autoYes: spec.autoYes,
                    verbose: spec.verbose
            )
        }, 'src/test/parser')
    }

}
