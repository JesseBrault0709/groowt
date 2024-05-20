package groowt.view.component.web.transpile;

import groovy.transform.Field;
import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration;
import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.ast.node.BodyNode;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import groowt.view.component.web.ast.node.PreambleNode;
import groowt.view.component.web.compiler.MultipleWebViewComponentCompileErrorsException;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileException;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.component.web.runtime.DefaultWebViewRenderContext;
import groowt.view.component.web.transpile.groovy.GroovyUtil;
import groowt.view.component.web.transpile.resolve.ClassLoaderComponentClassNodeResolver;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.SourceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static groowt.view.component.web.transpile.TranspilerUtil.*;
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

    private static final ClassNode FIELD_ANNOTATION = ClassHelper.make(Field.class);
    private static final ClassNode RENDER_CONTEXT_IMPLEMENTATION =
            ClassHelper.make(DefaultWebViewRenderContext.class);

    protected TranspilerConfiguration getConfiguration(
            WebViewComponentTemplateCompileUnit compileUnit,
            ModuleNode moduleNode,
            ClassLoader classLoader
    ) {
        return new DefaultTranspilerConfiguration(new ClassLoaderComponentClassNodeResolver(
                compileUnit, moduleNode, classLoader
        ));
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

    protected void handlePreamble(
            String templateClassName,
            PreambleNode preambleNode,
            ClassNode mainClassNode,
            WebViewComponentModuleNode moduleNode
    ) {
        final GroovyUtil.ConvertResult convertResult = GroovyUtil.convert(
                preambleNode.getGroovyCode().getAsValidGroovyCode()
        );

        WebViewComponentModuleNode.copyTo(convertResult.moduleNode(), moduleNode);

        if (convertResult.moduleNode().hasPackage()) {
            moduleNode.setPackage(convertResult.moduleNode().getPackage());
            mainClassNode.setName(moduleNode.getPackageName() + templateClassName);
        }

        final BlockStatement preambleBlock = convertResult.blockStatement();
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
                        templateClassName
                );
            }
            declarationsWithField.forEach(declaration -> {
                declaration.setDeclaringClass(mainClassNode);
            });
        }

        // move methods from script class
        final ClassNode scriptClass = convertResult.scriptClass();
        if (scriptClass != null) {
            scriptClass.getMethods().forEach(mainClassNode::addMethod);
        }

        // handle classes
        final List<ClassNode> classNodes = convertResult.classNodes();
        this.checkPreambleClasses(templateClassName, classNodes);
        classNodes.stream()
                .filter(classNode -> classNode != convertResult.scriptClass())
                .forEach(moduleNode::addClass);
    }

    // Cases:
    // - no preamble -> create our own class
    // - some preamble, but no script -> create our own class but use imports/packageName from preamble
    // - preamble with script -> use the script class from the converted preamble,
    // and don't forget to call run in our render method
    @Override
    public WebViewComponentSourceUnit transpile(
            ComponentTemplateCompilerConfiguration compilerConfiguration,
            WebViewComponentTemplateCompileUnit compileUnit,
            CompilationUnitNode compilationUnitNode,
            String templateClassName
    ) throws ComponentTemplateCompileException {
        final var groovyCompilerConfiguration = compilerConfiguration.getGroovyCompilerConfiguration();
        final var sourceUnit = new WebViewComponentSourceUnit(
                templateClassName,
                compileUnit.getGroovyReaderSource(),
                groovyCompilerConfiguration,
                compilerConfiguration.getGroovyClassLoader(),
                new ErrorCollector(groovyCompilerConfiguration)
        );

        final var moduleNode = new WebViewComponentModuleNode(sourceUnit);
        sourceUnit.setModuleNode(moduleNode);

        final String defaultPackageName = compileUnit.getDefaultPackageName();
        if (!defaultPackageName.trim().isEmpty()) {
            moduleNode.setPackageName(defaultPackageName);
        }

        moduleNode.addStarImport(GROOWT_VIEW_COMPONENT_WEB + ".lib");
        moduleNode.addImport(COMPONENT_TEMPLATE.getNameWithoutPackage(), COMPONENT_TEMPLATE);
        moduleNode.addImport(COMPONENT_CONTEXT_TYPE.getNameWithoutPackage(), COMPONENT_CONTEXT_TYPE);
        moduleNode.addStarImport("groowt.view.component.runtime");
        moduleNode.addStarImport(GROOWT_VIEW_COMPONENT_WEB + ".runtime");

        final ClassNode mainClassNode = new ClassNode(
                compileUnit.getDefaultPackageName() + templateClassName,
                ACC_PUBLIC,
                ClassHelper.OBJECT_TYPE
        );
        mainClassNode.setScript(true);
        mainClassNode.addInterface(TranspilerUtil.COMPONENT_TEMPLATE);

        moduleNode.addClass(mainClassNode);

        // preamble
        final PreambleNode preambleNode = compilationUnitNode.getPreambleNode();
        if (preambleNode != null) {
            this.handlePreamble(templateClassName, preambleNode, mainClassNode, moduleNode);
        }

        // getRenderer
        // params
        final Parameter componentContextParam = new Parameter(COMPONENT_CONTEXT_TYPE, COMPONENT_CONTEXT_NAME);
        final Parameter writerParam = new Parameter(COMPONENT_WRITER_TYPE, COMPONENT_WRITER_NAME);
        final VariableExpression renderContextVariable = new VariableExpression(
                RENDER_CONTEXT_NAME,
                WEB_VIEW_COMPONENT_RENDER_CONTEXT_TYPE
        );

        // closure body
        final BlockStatement renderBlock = new BlockStatement();

        final TranspilerState state = TranspilerState.withRootScope(
                componentContextParam,
                writerParam,
                renderContextVariable
        );
        renderBlock.setVariableScope(state.getCurrentScope());

        // init: construct RenderContext
        final ConstructorCallExpression renderContextConstructor = new ConstructorCallExpression(
                RENDER_CONTEXT_IMPLEMENTATION,
                new ArgumentListExpression(
                        new VariableExpression(componentContextParam), // component context
                        new VariableExpression(writerParam)
                )
        );
        final BinaryExpression renderContextAssignExpr = new DeclarationExpression(
                renderContextVariable,
                getAssignToken(),
                renderContextConstructor
        );
        renderBlock.addStatement(new ExpressionStatement(renderContextAssignExpr));

        // init: componentContext.renderContext = renderContext
        final BinaryExpression componentContextRenderContextAssign = new BinaryExpression(
                new PropertyExpression(new VariableExpression(componentContextParam), "renderContext"),
                getAssignToken(),
                renderContextVariable
        );
        renderBlock.addStatement(new ExpressionStatement(componentContextRenderContextAssign));

        // init: writer.renderContext = renderContext
        final BinaryExpression writerRenderContextAssign = new BinaryExpression(
                new PropertyExpression(new VariableExpression(writerParam), "renderContext"),
                getAssignToken(),
                renderContextVariable
        );
        renderBlock.addStatement(new ExpressionStatement(writerRenderContextAssign));

        // init: writer.componentContext = componentContext
        final BinaryExpression writerComponentContextAssign = new BinaryExpression(
                new PropertyExpression(new VariableExpression(writerParam), "componentContext"),
                getAssignToken(),
                new VariableExpression(componentContextParam)
        );
        renderBlock.addStatement(new ExpressionStatement(writerComponentContextAssign));

        // actual rendering of body
        final var configuration = this.getConfiguration(
                compileUnit,
                moduleNode,
                compilerConfiguration.getGroovyClassLoader()
        );
        final BodyNode bodyNode = compilationUnitNode.getBodyNode();
        if (bodyNode != null) {
            final var appendOrAddStatementFactory = configuration.getAppendOrAddStatementFactory();
            renderBlock.addStatement(
                    configuration.getBodyTranspiler()
                            .transpileBody(
                                    compilationUnitNode.getBodyNode(),
                                    (source, expr) -> appendOrAddStatementFactory.addOrAppend(
                                            source,
                                            state,
                                            action -> {
                                                if (action == AppendOrAddStatementFactory.Action.ADD) {
                                                    throw new WebViewComponentBugError(new IllegalStateException(
                                                            "Should not be adding from document root!"
                                                    ));
                                                }
                                                return expr;
                                            }
                                    ),
                                    state
                            )
            );
        }

        renderBlock.addStatement(new ReturnStatement(ConstantExpression.NULL));

        final ClosureExpression renderer = new ClosureExpression(
                new Parameter[] { componentContextParam, writerParam },
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

        if (state.hasErrors()) {
            final List<ComponentTemplateCompileException> errors = state.getErrors();
            if (errors.size() == 1) {
                throw new WebViewComponentTemplateCompileException(compileUnit, errors.getFirst());
            } else {
                throw new MultipleWebViewComponentCompileErrorsException(compileUnit, errors);
            }
        }

        return sourceUnit;
    }

}
