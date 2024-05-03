package groowt.view.web;

import groovy.lang.GroovyClassLoader;
import groowt.util.di.RegistryObjectFactory;
import groowt.view.component.CachingComponentTemplateCompiler;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ComponentTemplateCreateException;
import groowt.view.component.ViewComponent;
import groowt.view.web.antlr.CompilationUnitParseResult;
import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.transpile.DefaultGroovyTranspiler;
import groowt.view.web.transpile.DefaultTranspilerConfiguration;
import groowt.view.web.transpile.TranspilerConfiguration;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.io.AbstractReaderSource;
import org.codehaus.groovy.tools.GroovyClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultWebComponentTemplateCompiler extends CachingComponentTemplateCompiler {

    private final CompilerConfiguration configuration;
    private final String defaultPackageName;
    private final int phase;

    private GroovyClassLoader groovyClassLoader;

    public DefaultWebComponentTemplateCompiler(
            CompilerConfiguration configuration,
            String defaultPackageName
    ) {
        this(configuration, defaultPackageName, Phases.CLASS_GENERATION);
    }

    @ApiStatus.Internal
    public DefaultWebComponentTemplateCompiler(
            CompilerConfiguration configuration,
            String defaultPackageName,
            int phase
    ) {
        this.configuration = configuration;
        this.defaultPackageName = defaultPackageName;
        this.phase = phase;
    }

    protected GroovyClassLoader getGroovyClassLoader() {
        if (this.groovyClassLoader == null) {
            this.groovyClassLoader = new GroovyClassLoader(this.getClass().getClassLoader());
        }
        return this.groovyClassLoader;
    }

    public void setGroovyClassLoader(GroovyClassLoader groovyClassLoader) {
        this.groovyClassLoader = Objects.requireNonNull(groovyClassLoader);
    }

    public void useOwnClassLoader() {
        this.groovyClassLoader = null;
    }

    protected ComponentTemplate doCompile(Class<? extends ViewComponent> forClass, Reader reader) {
        return this.doCompile(forClass, reader, null);
    }

    protected ComponentTemplate doCompile(Class<? extends ViewComponent> forClass, Reader reader, @Nullable URI uri) {
        final CompilationUnitParseResult parseResult = ParserUtil.parseCompilationUnit(reader);

        // TODO: analysis

        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());

        final var groovyCompilationUnit = new CompilationUnit(this.configuration);
        final var transpiler = new DefaultGroovyTranspiler(
                groovyCompilationUnit,
                this.defaultPackageName,
                DefaultTranspilerConfiguration::new
        );

        final var ownerComponentName = forClass.getSimpleName();
        final var templateClassName = ownerComponentName + "Template";
        final var fqn = this.defaultPackageName + "." + templateClassName;

        final var readerSource = new AbstractReaderSource(this.configuration) {

            @Override
            public Reader getReader() throws IOException {
                reader.reset();
                return reader;
            }

            @Override
            public @Nullable URI getURI() {
                return uri;
            }

            @Override
            public void cleanup() {
                super.cleanup();
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };

        transpiler.transpile(cuNode, tokenList, ownerComponentName, readerSource);

        groovyCompilationUnit.compile(this.phase);

        final var classes = groovyCompilationUnit.getClasses();
        Class<?> templateClass = null;
        for (final GroovyClass groovyClass : classes) {
            if (groovyClass.getName().equals(fqn)) {
                if (templateClass == null) {
                    templateClass = this.getGroovyClassLoader().defineClass(
                            groovyClass.getName(), groovyClass.getBytes()
                    );
                } else {
                    throw new IllegalStateException("Somehow found two classes with same name.");
                }
            } else {
                this.getGroovyClassLoader().defineClass(
                        groovyClass.getName(), groovyClass.getBytes()
                );
            }
        }

        if (templateClass == null) {
            throw new IllegalStateException("Did not find templateClass");
        }

        try {
            return (ComponentTemplate) templateClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new ComponentTemplateCreateException(e, forClass, reader);
        }
    }

    @Override
    public ComponentTemplate compile(Class<? extends ViewComponent> forClass, File templateFile) {
        return this.getFromCacheOrElse(forClass, () -> {
            try {
                return this.doCompile(forClass, new FileReader(templateFile));
            } catch (FileNotFoundException e) {
                throw new ComponentTemplateCreateException(e, forClass, templateFile);
            }
        });
    }

    @Override
    public ComponentTemplate compile(Class<? extends ViewComponent> forClass, String template) {
        return this.getFromCacheOrElse(forClass, () -> this.doCompile(forClass, new StringReader(template)));
    }

    @Override
    public ComponentTemplate compile(Class<? extends ViewComponent> forClass, URI templateURI) {
        return this.getFromCacheOrElse(forClass, () -> {
            final Path path = Paths.get(templateURI);
            try {
                return this.doCompile(forClass, Files.newBufferedReader(path), templateURI);
            } catch (IOException e) {
                throw new ComponentTemplateCreateException(e, forClass, templateURI);
            }
        });
    }

    @Override
    public ComponentTemplate compile(Class<? extends ViewComponent> forClass, URL templateURL) {
        return this.getFromCacheOrElse(forClass, () -> {
            try {
                return this.doCompile(forClass, new InputStreamReader(templateURL.openStream()), templateURL.toURI());
            } catch (Exception e) {
                throw new ComponentTemplateCreateException(e, forClass, templateURL);
            }
        });
    }

    @Override
    public ComponentTemplate compile(Class<? extends ViewComponent> forClass, InputStream inputStream) {
        return this.getFromCacheOrElse(forClass, () -> this.doCompile(forClass, new InputStreamReader(inputStream)));
    }

    @Override
    public ComponentTemplate compile(Class<? extends ViewComponent> forClass, Reader reader) {
        return this.getFromCacheOrElse(forClass, () -> this.doCompile(forClass, reader));
    }

}
