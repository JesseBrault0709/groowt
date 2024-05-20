package groowt.view.component.web.tools;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.web.groovyc.WebViewComponentParserPluginFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(
        name = "groovyWvc",
        description = "Compile Groovy, Java, and WebViewComponent (.wvc) files together.",
        mixinStandardHelpOptions = true
)
public final class GroovyWvcCompiler implements Callable<Integer> {

    private static final Logger logger = LogManager.getLogger(GroovyWvcCompiler.class);

    @CommandLine.Parameters(
            arity = "1..*",
            description = "The files to compile, each ending with .groovy, .java, or .wvc."
    )
    private List<File> files;

    @CommandLine.Option(
            names = { "-d", "--out-dir" },
            description = "The output dir for classes.",
            defaultValue = "groovy-wvc-out"
    )
    private File outDir;

    @CommandLine.Option(
            names = { "-j", "--joint" },
            description = "Whether to joint-compile java files as well."
    )
    private boolean jointCompile;

    @CommandLine.Option(
            names = { "-L", "--log-level" },
            description = "The desired logging level.",
            defaultValue = "WARNING"
    )
    private String logLevel;

    public static void main(String[] args) {
        System.exit(new CommandLine(new GroovyWvcCompiler()).execute(args));
    }

    @Override
    public Integer call() {
        final Level logLevel = Level.toLevel(this.logLevel);
        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.getRootLogger().setLevel(logLevel);
        return this.doCompile();
    }

    public Integer doCompile() {
        final CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setPluginFactory(new WebViewComponentParserPluginFactory());
        final CompilationUnit compilationUnit = new CompilationUnit(configuration);

        configuration.setTargetDirectory(this.outDir);

        this.files.forEach(compilationUnit::addSource);
        this.files.forEach(file -> {
            if (file.isFile()) {
                try {
                    final URL url = file.getAbsoluteFile().getParentFile().toURI().toURL();
                    compilationUnit.getClassLoader().addURL(url);
                } catch (MalformedURLException e) {
                    logger.error("Could not create URL for file: {}", file);
                }
            }
        });

        if (this.jointCompile) {
            final File stubDir;
            try {
                stubDir = Files.createTempDirectory("groovy-wvc-generated-java-source-").toFile();
            } catch (IOException e) {
                logger.error("Could not create java stubs dir.");
                return 1;
            }
            final Map<String, Object> jointCompilationOptions = new HashMap<>();
            jointCompilationOptions.put("stubDir", stubDir);
            configuration.setJointCompilationOptions(jointCompilationOptions);
        }

        try {
            compilationUnit.compile();
        } catch (Exception e) {
            if (e instanceof CompilationFailedException compilationFailedException) {
                final Throwable cause = compilationFailedException.getCause();
                if (cause instanceof ComponentTemplateCompileException componentTemplateCompileException) {
                    logger.error(componentTemplateCompileException);
                    return 1;
                }
            }
            logger.error(e);
            return 1;
        }
        return 0;
    }

}
