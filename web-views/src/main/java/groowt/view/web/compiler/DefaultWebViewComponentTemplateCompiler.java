package groowt.view.web.compiler;

import groovy.lang.GroovyClassLoader;
import groowt.view.component.AbstractViewComponent;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.ViewComponent;
import groowt.view.component.compiler.CachingComponentTemplateCompiler;
import groowt.view.component.compiler.ComponentTemplateCompileErrorException;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.factory.ComponentTemplateSource;
import groowt.view.web.analysis.MismatchedComponentTypeAnalysis;
import groowt.view.web.analysis.MismatchedComponentTypeError;
import groowt.view.web.antlr.AntlrUtil;
import groowt.view.web.antlr.CompilationUnitParseResult;
import groowt.view.web.antlr.ParserUtil;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.DefaultAstBuilder;
import groowt.view.web.ast.DefaultNodeFactory;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.transpile.DefaultGroovyTranspiler;
import groowt.view.web.transpile.DefaultTranspilerConfiguration;
import groowt.view.web.util.SourcePosition;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.io.AbstractReaderSource;
import org.codehaus.groovy.tools.GroovyClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DefaultWebViewComponentTemplateCompiler extends CachingComponentTemplateCompiler
        implements WebViewComponentTemplateCompiler {

    protected static final class AnonymousWebViewComponent extends AbstractViewComponent {

        // DO NOT INSTANTIATE, this is merely a marker class
        private AnonymousWebViewComponent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContext(ComponentContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ComponentContext getContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected ComponentTemplate getTemplate() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void setTemplate(ComponentTemplate template) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void beforeRender() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void afterRender() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void renderTo(Writer out) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Class<? extends AbstractViewComponent> getSelfClass() {
            throw new UnsupportedOperationException();
        }

    }

    private final CompilerConfiguration configuration;
    private final String defaultPackageName;
    private final int phase;

    public DefaultWebViewComponentTemplateCompiler(
            GroovyClassLoader groovyClassLoader,
            CompilerConfiguration configuration,
            String defaultPackageName
    ) {
        this(groovyClassLoader, configuration, defaultPackageName, Phases.CLASS_GENERATION);
    }

    @ApiStatus.Internal
    public DefaultWebViewComponentTemplateCompiler(
            GroovyClassLoader groovyClassLoader,
            CompilerConfiguration configuration,
            String defaultPackageName,
            int phase
    ) {
        super(groovyClassLoader);
        this.configuration = configuration;
        this.defaultPackageName = defaultPackageName;
        this.phase = phase;
    }

    protected WebViewComponentTemplateCompileException getException(
            TerminalNode terminalNode,
            Class<? extends ViewComponent> forClass,
            Reader reader
    ) {
        final Token offending = terminalNode.getSymbol();
        return new WebViewComponentTemplateCompileException(
                "Compile error on token at " + SourcePosition.fromStartOfToken(offending).toStringLong() + ".",
                forClass,
                reader,
                offending
        );
    }

    protected WebViewComponentTemplateCompileException getException(
            ParserRuleContext parserRuleContext,
            Class<? extends ViewComponent> forClass,
            Reader reader
    ) {
        return new WebViewComponentTemplateCompileException(
                "Compile error at " + SourcePosition.fromStartOfToken(parserRuleContext.getStart()).toStringLong()
                        + ".",
                forClass,
                reader,
                parserRuleContext
        );
    }

    protected WebViewComponentTemplateCompileException mapToErrorException(
            Tree tree,
            Class<? extends ViewComponent> forClass,
            Reader reader
    ) {
        if (tree instanceof TerminalNode terminalNode) {
            return getException(terminalNode, forClass, reader);
        } else if (tree instanceof ParserRuleContext parserRuleContext) {
            return getException(parserRuleContext, forClass, reader);
        } else {
            return new WebViewComponentTemplateCompileException(
                    "Compile error with " + tree + ".",
                    forClass,
                    reader,
                    tree
            );
        }
    }

    protected WebViewComponentTemplateCompileException mapToErrorException(
            MismatchedComponentTypeError error,
            Class<? extends ViewComponent> forClass,
            Reader reader
    ) {
        return new WebViewComponentTemplateCompileException(
                error.getMessage(),
                forClass,
                reader,
                error.getComponent()
        );
    }

    protected ComponentTemplateCompileResult doCompile(
            Class<? extends ViewComponent> forClass,
            Reader reader,
            @Nullable URI uri
    ) throws ComponentTemplateCompileErrorException {
        final CompilationUnitParseResult parseResult = ParserUtil.parseCompilationUnit(reader);

        // check for parser/lexer errors
        final var parseErrors = AntlrUtil.findErrorNodes(parseResult.getCompilationUnitContext());
        if (!parseErrors.isEmpty()) {
            if (parseErrors.getErrorCount() == 1) {
                final var errorNode = parseErrors.getAll().getFirst();
                throw mapToErrorException(errorNode, forClass, reader);
            } else {
                final var errorExceptions = parseErrors.getAll().stream()
                        .map(errorNode -> mapToErrorException(errorNode, forClass, reader))
                        .toList();
                throw new MultipleWebViewComponentCompileErrorsException(errorExceptions, forClass, reader);
            }
        }

        // check for mismatched type errors
        final List<MismatchedComponentTypeError> mismatchedComponentTypeErrors =
                MismatchedComponentTypeAnalysis.check(parseResult.getCompilationUnitContext());

        if (!mismatchedComponentTypeErrors.isEmpty()) {
            if (mismatchedComponentTypeErrors.size() == 1) {
                throw mapToErrorException(mismatchedComponentTypeErrors.getFirst(), forClass, reader);
            } else {
                final var errorExceptions = mismatchedComponentTypeErrors.stream()
                        .map(error -> mapToErrorException(error, forClass, reader))
                        .toList();
                throw new MultipleWebViewComponentCompileErrorsException(
                        errorExceptions,
                        forClass,
                        reader
                );
            }
        }

        // build ast
        final var tokenList = new TokenList(parseResult.getTokenStream());
        final var astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList));
        final var cuNode = (CompilationUnitNode) astBuilder.build(parseResult.getCompilationUnitContext());

        // transpile to Groovy
        final var groovyCompilationUnit = new CompilationUnit(this.configuration);
        final var transpiler = new DefaultGroovyTranspiler(
                groovyCompilationUnit,
                this.defaultPackageName,
                DefaultTranspilerConfiguration::new
        );

        final var ownerComponentName = forClass != null ? forClass.getSimpleName() : "AnonymousComponent" + System.nanoTime();
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

        try {
            groovyCompilationUnit.compile(this.phase);
        } catch (CompilationFailedException compilationFailedException) {
            throw new WebViewComponentTemplateCompileException(
                    "Error while compiling Groovy in " + templateClassName + " for component class " +
                            forClass.getName() + ".",
                    compilationFailedException,
                    forClass,
                    forClass,
                    reader
            );
        }

        // get the classes
        final var allClasses = groovyCompilationUnit.getClasses();
        GroovyClass templateGroovyClass = null;
        final List<GroovyClass> otherClasses = new ArrayList<>();
        for (final GroovyClass groovyClass : allClasses) {
            if (groovyClass.getName().equals(fqn)) {
                if (templateGroovyClass != null) {
                    throw new IllegalStateException("Already found a templateGroovyClass.");
                }
                templateGroovyClass = groovyClass;
            } else {
                otherClasses.add(groovyClass);
            }
        }

        if (templateGroovyClass == null) {
            throw new IllegalStateException("Did not find templateClass");
        }

        return new ComponentTemplateCompileResult(templateGroovyClass, otherClasses);
    }

    @Override
    protected ComponentTemplateCompileResult doCompile(
            @Nullable ComponentTemplateSource source,
            Class<? extends ViewComponent> forClass,
            Reader sourceReader
    ) throws ComponentTemplateCompileErrorException {
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

    @Override
    public ComponentTemplateCompileResult compileAnonymous(ComponentTemplateSource source)
            throws ComponentTemplateCompileErrorException {
        return this.compile(AnonymousWebViewComponent.class, source);
    }

    @Override
    public ComponentTemplate compileAndGetAnonymous(ComponentTemplateSource source) throws ComponentTemplateCompileErrorException {
        return this.compileAndGet(AnonymousWebViewComponent.class, source);
    }

}
