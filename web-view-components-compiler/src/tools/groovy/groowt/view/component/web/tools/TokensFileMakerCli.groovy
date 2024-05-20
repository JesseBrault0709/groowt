package groowt.view.component.web.tools

import picocli.CommandLine

@CommandLine.Command(
        name = 'tokensFileMaker',
        description = 'Tokenize given input files and output files containing their tokens.',
        mixinStandardHelpOptions = true
)
class TokensFileMakerCli extends SourceFileProcessorSpec {

    static void main(String[] args) {
        System.exit(new CommandLine(new TokensFileMakerCli()).execute(args))
    }

    TokensFileMakerCli() {
        super({ SourceFileProcessorSpec spec ->
            new TokensFileMaker(
                    dryRun: spec.dryRun,
                    suffix: spec.suffix.orElse('_tokens'),
                    extension: spec.extension,
                    outputDirectory: spec.outputDirectory.orElse(new File('src/test/lexer/tokens-files')),
                    autoYes: spec.autoYes,
                    verbose: spec.verbose
            )
        }, 'src/test/lexer')
    }

}
