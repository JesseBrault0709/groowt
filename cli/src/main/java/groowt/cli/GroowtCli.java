package groowt.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;

@CommandLine.Command(
        name = "groowt",
        description = "The command line interface facilitating development of a Groowt project.",
        mixinStandardHelpOptions = true,
        version = "0.1.0",
        subcommands = { Generate.class }
)
public final class GroowtCli {

    private static final Logger logger = LoggerFactory.getLogger(GroowtCli.class);

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
        logger.info("Hello from Groowt! Version 0.1.0");
        System.exit(new CommandLine(new GroowtCli()).execute(args));
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public File getProjectDir() {
        return this.projectDir;
    }

}
