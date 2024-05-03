package groowt.view.web.transpile;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.node.BodyNode;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.transpile.PreambleTranspiler.PreambleResult;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.io.ReaderSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static groowt.view.web.transpile.TranspilerUtil.*;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * Takes a Groovy {@link CompilationUnit} in the constructor and adds a {@link SourceUnit} representing the target
 * Groovy code for the template represented by the AST passed to {@link #transpile}.
 * <p>
 * Note that while the terminology is similar, a Groovy {@link CompilationUnit} is distinct from our own
 * {@link CompilationUnitNode}.
 */
public class DefaultGroovyTranspiler implements GroovyTranspiler {

    private final CompilationUnit groovyCompilationUnit;
    private final String defaultPackageName;
    private final Supplier<? extends TranspilerConfiguration> configurationSupplier;

    public DefaultGroovyTranspiler(
            CompilationUnit groovyCompilationUnit,
            @Nullable String defaultPackageName,
            Supplier<? extends TranspilerConfiguration> configurationSupplier
    ) {
        this.groovyCompilationUnit = groovyCompilationUnit;
        this.defaultPackageName = defaultPackageName;
        this.configurationSupplier = configurationSupplier;
    }

    protected TranspilerConfiguration getConfiguration() {
        return this.configurationSupplier.get();
    }

    public @NotNull String getDefaultPackageName() {
        return this.defaultPackageName != null ? this.defaultPackageName : GROOWT_VIEW_WEB;
    }

    protected @NotNull String getPackageName(ModuleNode moduleNode) {
        if (moduleNode.hasPackageName()) {
            return moduleNode.getPackageName();
        } else {
            return this.getDefaultPackageName();
        }
    }

    // Cases:
    // - no preamble -> create our own class
    // - some preamble, but no script -> create our own class but use imports/packageName from preamble
    // - preamble with script -> use the script class from the converted preamble,
    // and don't forget to call run in our render method
    @Override
    public void transpile(
            CompilationUnitNode compilationUnitNode,
            TokenList tokens,
            String ownerComponentName,
            ReaderSource readerSource
    ) {
        final var configuration = this.getConfiguration();
        final String templateName = ownerComponentName + "Template";

        final var sourceUnit = new WebViewComponentSourceUnit(
                templateName,
                readerSource,
                this.groovyCompilationUnit.getConfiguration(),
                this.groovyCompilationUnit.getClassLoader(),
                this.groovyCompilationUnit.getErrorCollector()
        );
        final var moduleNode = new WebViewComponentModuleNode(sourceUnit);
        sourceUnit.setModuleNode(moduleNode);

        ClassNode mainClassNode;

        final PreambleResult preambleResult = configuration.getPreambleTranspiler().getPreambleResult(
                compilationUnitNode.getPreambleNode(),
                templateName,
                tokens
        );
        if (preambleResult.moduleNode() != null) {
            WebViewComponentModuleNode.copyTo(preambleResult.moduleNode(), moduleNode);
        }
        if (preambleResult.scriptClass() != null) {
            mainClassNode = preambleResult.scriptClass();
            // do not add it to moduleNode because it's already there
        } else {
            final String packageName = this.getPackageName(moduleNode);
            final String templateClassName = packageName + "." + templateName;

            mainClassNode = new ClassNode(
                    templateClassName,
                    ACC_PUBLIC,
                    ClassHelper.OBJECT_TYPE
            );
            mainClassNode.setScript(true);
            mainClassNode.addInterface(TranspilerUtil.COMPONENT_TEMPLATE);

            moduleNode.addClass(mainClassNode);
        }

        final var renderBlock = new BlockStatement();

        final TranspilerState state = TranspilerState.withDefaultRootScope();
        renderBlock.setVariableScope(state.currentScope());

        final BodyNode bodyNode = compilationUnitNode.getBodyNode();
        if (bodyNode != null) {
            final var outStatementFactory = configuration.getOutStatementFactory();
            renderBlock.addStatement(
                    configuration.getBodyTranspiler()
                            .transpileBody(
                                    compilationUnitNode.getBodyNode(),
                                    (ignored, expr) -> outStatementFactory.create(expr),
                                    state
                            )
            );
        }

        final ClosureExpression renderer = new ClosureExpression(
                new Parameter[] {
                        (Parameter) state.getDeclaredVariable(CONTEXT),
                        (Parameter) state.getDeclaredVariable(OUT)
                },
                renderBlock
        );
        final Statement returnRendererStmt = new ReturnStatement(renderer);

        final var voidClosure = ClassHelper.CLOSURE_TYPE.getPlainNodeReference();
        voidClosure.setGenericsTypes(new GenericsType[] { new GenericsType(ClassHelper.void_WRAPPER_TYPE) });

        final var getRenderer = new MethodNode(
                GET_RENDERER,
                ACC_PUBLIC,
                voidClosure,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                returnRendererStmt
        );
        mainClassNode.addMethod(getRenderer);

        this.groovyCompilationUnit.addSource(sourceUnit);
    }

}
