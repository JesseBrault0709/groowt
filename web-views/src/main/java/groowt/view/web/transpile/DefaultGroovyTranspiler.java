package groowt.view.web.transpile;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.node.BodyNode;
import groowt.view.web.ast.node.CompilationUnitNode;
import groowt.view.web.ast.node.PreambleNode;
import groowt.view.web.transpile.util.GroovyUtil;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.io.ReaderSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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

    private static final Logger logger = LoggerFactory.getLogger(DefaultGroovyTranspiler.class);

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

    protected void checkPreambleClasses(String templateName, List<ClassNode> classNodes) {
        final ClassNode offending = classNodes.stream()
                .filter(classNode -> classNode.getName().equals(templateName))
                .findAny()
                .orElse(null);
        if (offending != null) {
            throw new IllegalArgumentException(
                    templateName + " cannot define itself in the template. " +
                            "Remove the class with that name."
            );
        }
    }

    protected List<InnerClassNode> convertPreambleClassesToInnerClasses(ClassNode mainClassNode, List<ClassNode> classNodes) {
        final List<InnerClassNode> result = new ArrayList<>();
        for (final var classNode : classNodes) {
            if (classNode instanceof InnerClassNode innerClassNode) {
                result.add(innerClassNode);
            } else {
                final InnerClassNode icn = new InnerClassNode(
                        mainClassNode,
                        mainClassNode.getName() + "." + classNode.getNameWithoutPackage(),
                        classNode.getModifiers(),
                        classNode.getSuperClass(),
                        classNode.getInterfaces(),
                        classNode.getMixins()
                );
                icn.setDeclaringClass(mainClassNode);
            }
        }
        return result;
    }

    protected void handlePreamble(
            String templateName,
            PreambleNode preambleNode,
            ClassNode mainClassNode,
            WebViewComponentModuleNode moduleNode
    ) {
        final GroovyUtil.ConvertResult preambleConvert = GroovyUtil.convert(
                preambleNode.getGroovyCode().getAsValidGroovyCode()
        );

        WebViewComponentModuleNode.copyTo(preambleConvert.moduleNode(), moduleNode);

        final BlockStatement preambleBlock = preambleConvert.blockStatement();
        if (preambleBlock != null) {
            // Fields
            final List<Statement> preambleStatements = preambleBlock.getStatements();
            final List<DeclarationExpression> declarationsWithField = preambleStatements.stream()
                    .filter(statement -> statement instanceof ExpressionStatement)
                    .map(ExpressionStatement.class::cast)
                    .map(ExpressionStatement::getExpression)
                    .filter(expression -> expression instanceof DeclarationExpression)
                    .map(DeclarationExpression.class::cast)
                    .filter(declarationExpression ->
                            !declarationExpression.getAnnotations(FIELD_ANNOTATION).isEmpty()
                    )
                    .toList();
            if (declarationsWithField.size() != preambleStatements.size()) {
                logger.warn(
                        "{} contains script statements which are not supported. " +
                                "Currently, only classes, methods, and field declarations " +
                                "(marked with @groovy.transform.Field) " +
                                "are supported. The rest will be ignored.",
                        templateName
                );
            }
            declarationsWithField.forEach(declaration -> {
                declaration.setDeclaringClass(mainClassNode);
            });
        }

        // move methods from script class
        final ClassNode scriptClass = preambleConvert.scriptClass();
        if (scriptClass != null) {
            scriptClass.getMethods().forEach(mainClassNode::addMethod);
        }

        // handle classes
        final List<ClassNode> classNodes = preambleConvert.classNodes();
        this.checkPreambleClasses(templateName, classNodes);
        final List<ClassNode> toInner = classNodes.stream()
                .filter(classNode -> classNode != preambleConvert.scriptClass())
                .filter(classNode -> !classNode.isScript())
                .toList();
        final List<InnerClassNode> innerClassNodes = this.convertPreambleClassesToInnerClasses(mainClassNode, toInner);
        innerClassNodes.forEach(moduleNode::addClass);
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

        final String packageName = this.getPackageName(moduleNode);
        moduleNode.setPackageName(packageName);

        final ClassNode mainClassNode = new ClassNode(
                packageName + "." + templateName,
                ACC_PUBLIC,
                ClassHelper.OBJECT_TYPE
        );
        mainClassNode.setScript(true);
        mainClassNode.addInterface(TranspilerUtil.COMPONENT_TEMPLATE);

        moduleNode.addClass(mainClassNode);

        // preamble
        final PreambleNode preambleNode = compilationUnitNode.getPreambleNode();
        if (preambleNode != null) {
            this.handlePreamble(templateName, preambleNode, mainClassNode, moduleNode);
        }

        // renderer
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

        // getRenderer()
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
