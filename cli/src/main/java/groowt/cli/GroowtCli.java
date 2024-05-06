package groowt.cli;

import picocli.CommandLine;

import java.io.File;

@CommandLine.Command(
        name = "groowt",
        description = "The command line interface facilitating development of a Groowt project.",
        mixinStandardHelpOptions = true,
        version = "0.1.0",
        subcommands = { Generate.class }
)
public class GroowtCli {

    @CommandLine.Option(
            names = { "-v", "--verbose" },
            description = "Log verbosely to standard out."
    )
    private boolean verbose;

    @CommandLine.Option(
            names = { "--projectDir" },
            defaultValue = ".",
            description = "The root directory of the groowt project."
    )
    private File projectDir;

    public static void main(String[] args) {
        System.out.println("Hello from Groowt!");
        System.exit(new CommandLine(new GroowtCli()).execute(args));
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public File getProjectDir() {
        return this.projectDir;
    }

}
