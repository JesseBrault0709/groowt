package groowt.view.web;

import groovy.lang.GroovyClassLoader;
import groowt.view.component.*;
import groowt.view.component.compiler.CachingComponentTemplateCompiler;
import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.factory.ComponentTemplateSource;
import groowt.view.web.antlr.CompilationUnitParseResult;
import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.transpile.DefaultGroovyTranspiler;
import groowt.view.web.transpile.DefaultTranspilerConfiguration;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.io.AbstractReaderSource;
import org.codehaus.groovy.tools.GroovyClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class DefaultWebViewComponentTemplateCompiler extends CachingComponentTemplateCompiler
        implements WebViewComponentTemplateCompiler {

    private final CompilerConfiguration configuration;
    private final String defaultPackageName;
    private final int phase;

    private GroovyClassLoader groovyClassLoader;

    public DefaultWebViewComponentTemplateCompiler(CompilerConfiguration configuration, String defaultPackageName) {
        this(configuration, defaultPackageName, Phases.CLASS_GENERATION);
    }

    @ApiStatus.Internal
    public DefaultWebViewComponentTemplateCompiler(
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

    @Override
    protected ComponentTemplate doCompile(
            @Nullable ComponentTemplateSource source,
            @Nullable Class<? extends ViewComponent> forClass,
            Reader sourceReader
    ) {
        if (source instanceof ComponentTemplateSource.URISource uriSource) {
            return this.doCompile(forClass, sourceReader, uriSource.templateURI());
        } else if (source instanceof ComponentTemplateSource.URLSource urlSource) {
            try {
                return this.doCompile(forClass, sourceReader, urlSource.templateURL().toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            return this.doCompile(forClass, sourceReader, null);
        }
    }

    protected ComponentTemplate doCompile(
            @Nullable Class<? extends ViewComponent> forClass,
            Reader reader,
            @Nullable URI uri
    ) {
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

        final var ownerComponentName = forClass != null ? forClass.getSimpleName() : "AnonymousComponent";
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
            throw new ComponentTemplateCompileException(e, forClass, reader);
        }
    }

    @Override
    public ComponentTemplate compileAnonymous(Reader reader) {
        return this.doCompile(null, null, reader);
    }

}
